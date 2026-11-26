package com.example.personal_management_app.data.repositories

import com.example.personal_management_app.core.service.SessionManager
import com.example.personal_management_app.data.entities.NoteEntity
import com.example.personal_management_app.data.entities.NoteReminder
import com.example.personal_management_app.data.entities.ReminderRepeat
import com.example.personal_management_app.data.local.AppDatabase
import com.example.personal_management_app.data.remote.BackendApi
import com.example.personal_management_app.data.remote.CreateNoteRequest
import com.example.personal_management_app.data.remote.NoteDto
import com.example.personal_management_app.data.remote.NoteTextStyleDto
import com.example.personal_management_app.data.remote.SetArchiveStatusRequest
import com.example.personal_management_app.data.remote.SetPinStatusRequest
import com.example.personal_management_app.data.remote.SetReminderRequest
import com.example.personal_management_app.data.remote.UpdateNoteRequest
import com.example.personal_management_app.dtos.NoteTextStyle
import com.example.personal_management_app.ui.screen.reminder.ReminderScheduler
import com.example.personal_management_app.utils.matchesQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.io.IOException
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class NoteRepository @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context,
    private val db: AppDatabase,
    private val api: BackendApi,
    private val requests: RepositoryRequests,
    private val scheduler: ReminderScheduler,
    private val sessionManager: SessionManager
) {
    private val syncPrefs = context.getSharedPreferences("pending_note_deletes", android.content.Context.MODE_PRIVATE)
    private fun pending(uid: String) = syncPrefs.getStringSet(uid, emptySet()).orEmpty().toSet()

    private suspend fun sendPendingDelete(uid: String, id: String) {
        val response = api.note.deleteNote(id)
        if (response.code() == 403) {
            syncPrefs.edit { putStringSet(uid, pending(uid) - id) }
        }
        if (response.code() != 404) response.checkSuccess()
        syncPrefs.edit { putStringSet(uid, pending(uid) - id)}
    }

    private val noteDao = db.noteDao()
    private val versions = mutableMapOf<String, Long>()
    val currentUserId get() = sessionManager.sessionState.value?.userId

    fun getNotesFlow(): Flow<List<NoteEntity>> = sessionManager.sessionState.flatMapLatest { session ->
        if (session == null) flowOf(emptyList()) else {
            requests.submit { uid -> refresh(uid) }
            noteDao.getNotesFlowByUserId(session.userId)
        }
    }

    fun getTrashFlow(): Flow<List<NoteEntity>> = sessionManager.sessionState.flatMapLatest { session ->
        session?.let { noteDao.getTrashNotesFlow(it.userId) } ?: flowOf(emptyList())
    }

    internal suspend fun refresh(uid: String) {
        for (id in pending(uid)) sendPendingDelete(uid, id)
        val prefix = "reminder:$uid:"
        for (key in syncPrefs.all.keys.filter { it.startsWith(prefix) }) {
            sendReminder(uid, key.removePrefix(prefix))
        }
        //TODO: Must use getAllNoteByUserId
        val remote = api.note.getAllNotes().payload() + api.note.getTrashItems().payload()
        val notes = remote.filter { it.id !in pending(uid) }.map { it.entity(uid).withPendingReminder() }
        val ids = notes.mapTo(mutableSetOf()) { it.id }
        val synced = syncPrefs.getStringSet("synced:$uid", emptySet()).orEmpty()
        val removed = noteDao.getAllRaw(uid).filter { it.id in synced && it.id !in ids && it.id !in pending(uid) }
        db.runInTransaction {
            removed.forEach { noteDao.deleteNoteById(it.id) }
            notes.forEach(noteDao::insertNote)
        }
        syncPrefs.edit { putStringSet("synced:$uid", ids + pending(uid)) }
        remote.forEach { dto -> dto.version?.let { versions[requireNotNull(dto.id)] = it } }
        removed.forEach { scheduler.cancel(it.id) }
        scheduler.sync(notes)
    }

    private fun NoteTextStyleDto?.style() = NoteTextStyle(this?.fontSize ?: 16f,
        this?.bold ?: false, this?.italic ?: false, this?.underline ?: false)
    private fun NoteTextStyle.dto() = NoteTextStyleDto(fontSize, bold, italic, underline)
    private fun NoteDto.entity(uid: String) = NoteEntity(requireNotNull(id), uid, title.orEmpty(),
        titleStyle.style(), content.orEmpty(), contentStyle.style(), tags.orEmpty().mapNotNull { it.name },
        backgroundColor ?: "0xFFFFF8D6", pinned == true, archivedAt?.let { Instant.parse(it).toEpochMilli() },
        deletedAt?.let { Instant.parse(it).toEpochMilli() }, reminderAt?.let { at ->
            NoteReminder(Instant.parse(at).toEpochMilli(), ReminderRepeat.entries.firstOrNull {
                it.name.equals(reminderRepeat, true)
            } ?: ReminderRepeat.Once)
        })

    private fun store(uid: String, dto: NoteDto) {
        if (dto.id in pending(uid)) return
        val note = dto.entity(uid).withPendingReminder()
        dto.version?.let { versions[note.id] = it }
        noteDao.insertNote(note)
        val key = "synced:$uid"
        syncPrefs.edit {
            putStringSet(
                key,
                syncPrefs.getStringSet(key, emptySet()).orEmpty() + note.id
            )
        }
        scheduler.apply(note)
    }

    private fun tagIds(uid: String, note: NoteEntity) = note.tags.map { name ->
        db.tagDao().getTagByName(uid, name)?.id ?: throw IOException("Không tìm thấy nhãn: $name")
    }

    fun insert(note: NoteEntity) = requests.submit { uid -> create(uid, note) }

    suspend fun saveDraft(note: NoteEntity, isNew: Boolean) = requests.execute { uid ->
        if (isNew) create(uid, note) else replace(uid, note)
    }

    private suspend fun create(uid: String, note: NoteEntity) {
        val body = CreateNoteRequest(note.title, note.content, note.titleStyle.dto(), note.contentStyle.dto(),
            note.backgroundColor, note.isPinned, note.reminder?.atMillis?.let { Instant.ofEpochMilli(it).toString() },
            note.reminder?.repeat?.name?.uppercase(), tagIds(uid, note))
        val created = api.note.createNote(body).payload()
        store(uid, created)
        if (note.archivedAt != null) store(uid, api.note.setNoteArchived(requireNotNull(created.id), SetArchiveStatusRequest(true)).payload())
    }

    fun update(note: NoteEntity) = requests.submit { uid -> replace(uid, note) }

    private suspend fun replace(uid: String, note: NoteEntity) {
        val body = UpdateNoteRequest(note.title, note.content, note.titleStyle.dto(), note.contentStyle.dto(),
            note.backgroundColor, note.isPinned, note.reminder?.atMillis?.let { Instant.ofEpochMilli(it).toString() },
            note.reminder?.repeat?.name?.uppercase(), tagIds(uid, note), versions[note.id])
        store(uid, api.note.updateNote(note.id, body).payload())
    }

    fun getList() = currentUserId?.let { noteDao.getNotesByUserId(it).toMutableList() } ?: mutableListOf()
    fun getTrashItems(query: String = "") = currentUserId?.let { noteDao.getTrashNotes(it) }.orEmpty()
        .filter { query.isBlank() || it.matchesQuery(query) }
    fun get(noteId: String) = noteDao.getNoteById(noteId)?.takeIf { it.userId == currentUserId }
    fun countByTag(name: String) = getList().count { name in it.tags }

    fun pin(noteId: String, status: Boolean) = requests.submit { uid ->
        store(uid, api.note.setNotePinned(noteId, SetPinStatusRequest(status)).payload())
    }
    fun setArchived(noteId: String, archivedAt: Long?) = requests.submit { uid ->
        store(uid, api.note.setNoteArchived(noteId, SetArchiveStatusRequest(archivedAt != null)).payload())
    }
    private fun reminderKey(uid: String, id: String) = "reminder:$uid:$id"

    private fun NoteEntity.withPendingReminder(): NoteEntity {
        val encoded = syncPrefs.getString(reminderKey(userId, id), null) ?: return this
        return copy(reminder = kotlinx.serialization.json.Json.decodeFromString<NoteReminder?>(encoded))
    }

    private suspend fun sendReminder(uid: String, id: String) {
        val key = reminderKey(uid, id)
        val encoded = syncPrefs.getString(key, null) ?: return
        val reminder = kotlinx.serialization.json.Json.decodeFromString<NoteReminder?>(encoded)
        val result = if (reminder == null) api.noteReminder.clearNoteReminder(id)
            else api.noteReminder.setNoteReminder(id, SetReminderRequest(
                Instant.ofEpochMilli(reminder.atMillis).toString(), reminder.repeat.name.uppercase()))
        if (result.code() in setOf(403, 404) && syncPrefs.getString(key, null) == encoded) {
            syncPrefs.edit { remove(key) }
        }
        val dto = result.payload()
        if (syncPrefs.getString(key, null) == encoded) {
            syncPrefs.edit { remove(key) }
            store(uid, dto)
        }
    }

    fun setReminder(noteId: String, reminder: NoteReminder?) {
        val note = get(noteId) ?: return
        val key = reminderKey(note.userId, noteId)
        syncPrefs.edit { putString(key, kotlinx.serialization.json.Json.encodeToString(reminder)) }
        val updated = note.copy(reminder = reminder)
        noteDao.updateNote(updated)
        scheduler.apply(updated)
        requests.submit { uid -> sendReminder(uid, noteId) }
    }
    fun delete(noteId: String) {
        val note = get(noteId) ?: return
        syncPrefs.edit {
            remove(reminderKey(note.userId, noteId)).putStringSet(
                note.userId,
                pending(note.userId) + noteId
            )
        }
        noteDao.updateNote(note.copy(deletedAt = System.currentTimeMillis(), archivedAt = null, isPinned = false))
        scheduler.cancel(noteId)
        requests.submit { uid ->
            if (noteId in pending(uid)) sendPendingDelete(uid, noteId)
        }
    }
    fun restore(noteId: String) = requests.submit { uid ->
        if (noteId in pending(uid)) sendPendingDelete(uid, noteId)
        store(uid, api.note.restoreNote(noteId).payload())
    }
    fun deleteForever(noteId: String) = requests.submit {
        api.note.deleteNotePermanently(noteId).checkSuccess()
        noteDao.deleteNoteById(noteId)
        scheduler.cancel(noteId)
    }
    fun emptyTrash() = requests.submit { uid ->
        for (id in pending(uid)) sendPendingDelete(uid, id)
        api.note.emptyTrash().checkSuccess()
        refresh(uid)
    }
}

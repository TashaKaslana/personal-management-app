package com.example.personal_management_app.data.repositories

import com.example.personal_management_app.core.service.SessionManager
import com.example.personal_management_app.data.local.dao.TagDao
import com.example.personal_management_app.data.entities.TagEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepository @Inject constructor(
    private val tagDao: TagDao,
    private val noteRepository: NoteRepository,
    private val sessionManager: SessionManager
) {
    val currentUserId: String?
        get() = sessionManager.sessionState.value?.userId

    @OptIn(ExperimentalCoroutinesApi::class)
    val tags: StateFlow<List<TagEntity>> = sessionManager.sessionState
        .flatMapLatest { session ->
            val uid = session?.userId
            if (uid != null) {
                tagDao.getTagsFlowByUserId(uid)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = sessionManager.sessionState.value?.userId?.let { tagDao.getTagsByUserId(it) } ?: emptyList()
        )

    fun getList(): List<TagEntity> {
        val uid = currentUserId ?: return emptyList()
        return tagDao.getTagsByUserId(uid)
    }

    fun add(name: String, color: String): TagEntity? {
        val uid = currentUserId ?: return null
        val tag = TagEntity(
            id = UUID.randomUUID().toString(),
            userId = uid,
            name = name.trim(),
            color = color
        )
        tagDao.insertTag(tag)
        return tag
    }

    fun update(id: String, name: String, color: String) {
        val current = tagDao.getTagById(id) ?: return
        val nextName = name.trim()
        if (current.name != nextName) {
            noteRepository.renameTag(current.name, nextName)
        }
        tagDao.updateTag(current.copy(name = nextName, color = color))
    }

    fun delete(id: String) {
        val tag = tagDao.getTagById(id) ?: return
        tagDao.deleteTagById(id)
        noteRepository.clearTag(tag.name)
    }

    fun noteCount(name: String): Int = noteRepository.countByTag(name)
}

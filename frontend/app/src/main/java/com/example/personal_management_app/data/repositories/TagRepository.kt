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
import com.example.personal_management_app.data.remote.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepository @Inject constructor(
    private val api: BackendApi,
    private val requests: RepositoryRequests,
    private val tagDao: TagDao,
    private val noteRepository: NoteRepository,
    private val sessionManager: SessionManager
) {
    private val syncedIds = mutableMapOf<String, Set<String>>()
    val currentUserId: String?
        get() = sessionManager.sessionState.value?.userId

    @OptIn(ExperimentalCoroutinesApi::class)
    val tags: StateFlow<List<TagEntity>> = sessionManager.sessionState
        .flatMapLatest { session ->
            val uid = session?.userId
            if (uid != null) {
                requests.submit { userId ->
                    //TODO: Must replace by getTagByUserId
                    val remote = api.tag.getAllTags().payload().map { it.entity(userId) }
                    val ids = remote.mapTo(mutableSetOf()) { it.id }
                    tagDao.insertAll(remote)
                    tagDao.getTagsByUserId(userId).filter { it.id in syncedIds[userId].orEmpty() && it.id !in ids }
                        .forEach { tagDao.deleteTagById(it.id) }
                    syncedIds[userId] = ids
                }
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

    private fun TagDto.entity(uid: String) =
        TagEntity(requireNotNull(id), uid, requireNotNull(name), color ?: "0xFF64B5F6")

    suspend fun add(name: String, color: String) = requests.execute { uid ->
        val tag = api.tag.createTag(CreateTagRequest(name.trim(), color)).payload().entity(uid)
        tagDao.insertTag(tag)
        tag
    }

    suspend fun update(id: String, name: String, color: String) = requests.execute { uid ->
        val updated = api.tag.updateTag(id, CreateTagRequest(name.trim(), color)).payload()
        tagDao.insertTag(updated.entity(uid))
        noteRepository.refresh(uid)
    }

    fun delete(id: String) = requests.submit { uid ->
        api.tag.deleteTag(id).checkSuccess()
        tagDao.deleteTagById(id)
        noteRepository.refresh(uid)
    }

    fun noteCount(name: String): Int = noteRepository.countByTag(name)
}

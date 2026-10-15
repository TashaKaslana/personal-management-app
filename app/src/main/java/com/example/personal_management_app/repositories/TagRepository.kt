package com.example.personal_management_app.repositories

import com.example.personal_management_app.entites.TagEntity
import com.example.personal_management_app.entites.tagColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepository @Inject constructor(
    private val noteRepository: NoteRepository
) {
    private val items = mutableListOf(
        TagEntity(id = "tag-work", name = "Công việc", color = tagColors[4]),
        TagEntity(id = "tag-travel", name = "Du lịch", color = tagColors[6])
    )
    private val _tags = MutableStateFlow(items.toList())
    val tags = _tags.asStateFlow()

    fun getList(): List<TagEntity> = items.toList()

    fun add(name: String, color: String): TagEntity {
        val tag = TagEntity(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            color = color
        )
        items.add(tag)
        publish()
        return tag
    }

    fun update(id: String, name: String, color: String) {
        val index = items.indexOfFirst { it.id == id }
        if (index == -1) return
        val current = items[index]
        val nextName = name.trim()
        if (current.name != nextName) {
            noteRepository.renameTag(current.name, nextName)
        }
        items[index] = current.copy(name = nextName, color = color)
        publish()
    }

    fun delete(id: String) {
        val tag = items.find { it.id == id } ?: return
        items.remove(tag)
        noteRepository.clearTag(tag.name)
        publish()
    }

    private fun publish() {
        _tags.value = items.toList()
    }

    fun noteCount(name: String): Int = noteRepository.countByTag(name)
}

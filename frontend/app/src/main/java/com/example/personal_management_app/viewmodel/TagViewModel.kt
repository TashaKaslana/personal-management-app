package com.example.personal_management_app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.personal_management_app.entities.TagEntity
import com.example.personal_management_app.entities.tagColors
import com.example.personal_management_app.repositories.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val repository: TagRepository
) : ViewModel() {
    val tags = repository.tags

    fun filter(query: String): List<TagEntity> {
        return tags.value.filter { tag ->
            query.isBlank() || tag.name.contains(query, ignoreCase = true)
        }
    }

    fun noteCount(name: String): Int = repository.noteCount(name)

    fun save(id: String?, name: String, color: String): String? {
        val trimmed = name.trim()

        if (trimmed.isBlank()) return "Tên nhãn không được để trống"

        val taken = tags.value.any { it.id != id && it.name.equals(trimmed, ignoreCase = true) }
        if (taken) return "Nhãn này đã tồn tại"

        val safeColor = color.takeIf { it in tagColors } ?: tagColors.first()

        if (id == null) {
            repository.add(trimmed, safeColor)
        } else {
            repository.update(id, trimmed, safeColor)
        }
        return null
    }

    fun delete(id: String) {
        repository.delete(id)
    }
}

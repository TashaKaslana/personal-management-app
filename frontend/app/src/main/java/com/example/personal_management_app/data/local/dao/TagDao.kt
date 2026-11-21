package com.example.personal_management_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.personal_management_app.entities.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Query("SELECT * FROM tags WHERE userId = :userId")
    fun getTagsFlowByUserId(userId: String): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE userId = :userId")
    fun getTagsByUserId(userId: String): List<TagEntity>

    @Query("SELECT * FROM tags WHERE id = :id LIMIT 1")
    fun getTagById(id: String): TagEntity?

    @Query("SELECT * FROM tags WHERE userId = :userId AND name = :name LIMIT 1")
    fun getTagByName(userId: String, name: String): TagEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTag(tag: TagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tags: List<TagEntity>)

    @Update
    fun updateTag(tag: TagEntity)

    @Query("DELETE FROM tags WHERE id = :id")
    fun deleteTagById(id: String)
}

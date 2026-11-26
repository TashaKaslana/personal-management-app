package com.example.personal_management_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.personal_management_app.data.entities.FeedbackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFeedback(feedback: FeedbackEntity)

    @Query("SELECT * FROM feedbacks WHERE userId = :userId ORDER BY createdAt DESC")
    fun getFeedbacksFlow(userId: String): Flow<List<FeedbackEntity>>

    @Query("SELECT * FROM feedbacks WHERE userId = :userId ORDER BY createdAt DESC")
    fun getFeedbacks(userId: String): List<FeedbackEntity>

    @Query("DELETE FROM feedbacks WHERE id = :id")
    fun deleteFeedbackById(id: String)
}

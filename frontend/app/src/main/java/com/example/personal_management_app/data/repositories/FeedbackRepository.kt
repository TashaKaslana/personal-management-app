package com.example.personal_management_app.data.repositories

import com.example.personal_management_app.core.service.SessionManager
import com.example.personal_management_app.data.local.dao.FeedbackDao
import com.example.personal_management_app.data.entities.FeedbackEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackRepository @Inject constructor(
    private val feedbackDao: FeedbackDao,
    private val sessionManager: SessionManager
) {
    val currentUserId: String?
        get() = sessionManager.sessionState.value?.userId

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getFeedbacksFlow(): Flow<List<FeedbackEntity>> = sessionManager.sessionState.flatMapLatest { session ->
        val uid = session?.userId
        if (uid != null) {
            feedbackDao.getFeedbacksFlow(uid)
        } else {
            flowOf(emptyList())
        }
    }

    suspend fun submitFeedback(
        type: String,
        content: String,
        deviceInfo: String?
    ): Boolean {
        val uid = currentUserId ?: "anonymous"
        val feedback = FeedbackEntity(
            userId = uid,
            feedbackType = type,
            content = content,
            deviceInfo = deviceInfo,
            createdAt = System.currentTimeMillis()
        )
        feedbackDao.insertFeedback(feedback)
        return true
    }
}

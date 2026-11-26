package com.example.personal_management_app.data.repositories

import android.content.Context
import android.widget.Toast
import com.example.personal_management_app.core.service.SessionManager
import com.example.personal_management_app.data.remote.ApiResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

internal fun <T> Response<ApiResponse<T>>.checkSuccess() {
    if (!isSuccessful) throw HttpException(this)
    if (body()?.success == false) throw IOException(body()?.message)
}

internal fun <T> Response<ApiResponse<T>>.payload(): T {
    checkSuccess()
    return body()?.data ?: throw IOException("Máy chủ không trả về dữ liệu")
}

// Screen navigation can finish before an edit is acknowledged by the server.
@Singleton
class RepositoryRequests @Inject constructor(
    private val sessions: SessionManager,
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val mutex = Mutex()

    suspend fun <T> execute(action: suspend (String) -> T): T {
        val session = sessions.sessionState.value ?: throw IOException("Vui lòng đăng nhập")
        return mutex.withLock {
            if (sessions.sessionState.value != session) throw IOException("Phiên đăng nhập đã thay đổi")
            coroutineScope {
                val operation = this
                val observer = launch(start = CoroutineStart.UNDISPATCHED) {
                    sessions.sessionState.collect { if (it != session) operation.cancel() }
                }
                try {
                    action(session.userId)
                } finally {
                    observer.cancel()
                }
            }
        }
    }

    fun submit(action: suspend (String) -> Unit) {
        scope.launch {
            try {
                execute(action)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}

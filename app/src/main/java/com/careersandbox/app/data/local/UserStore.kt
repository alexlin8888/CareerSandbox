package com.careersandbox.app.data.local

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.careersandbox.app.data.remote.UserProfileResponse
import com.careersandbox.app.data.repository.RemoteUserRepository

object UserStore {
    // Compose-observable current user; null = not loaded yet.
    // Any screen reading this recomposes automatically when it changes.
    var me: UserProfileResponse? by mutableStateOf(null)
        private set

    // Fetch /users/me and cache it here
    suspend fun refresh(): Result<UserProfileResponse> {
        val result = RemoteUserRepository().getMe()
        result.onSuccess { me = it }
        return result
    }

    // Called on logout
    fun clear() {
        me = null
    }
}
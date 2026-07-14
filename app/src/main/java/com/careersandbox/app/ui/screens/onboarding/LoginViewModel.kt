package com.careersandbox.app.ui.screens.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careersandbox.app.data.local.SessionManager
import com.careersandbox.app.data.local.UserStore
import com.careersandbox.app.data.repository.AuthRepository
import com.careersandbox.app.data.repository.RemoteAuthRepository
import kotlinx.coroutines.launch

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data class Success(val token: String, val userId: String) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel(
    private val repo: AuthRepository = RemoteAuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf<LoginUiState>(LoginUiState.Idle)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            uiState = LoginUiState.Loading
            repo.login(email.trim(), password)
                .onSuccess {
                    SessionManager.save(it.token, it.userId)
                    UserStore.refresh() // load the real profile before entering Home
                    uiState = LoginUiState.Success(it.token, it.userId)
                }
                .onFailure { uiState = LoginUiState.Error(it.message ?: "登入失敗，請稍後再試") }
        }
    }
}
package com.careersandbox.app.ui.screens.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careersandbox.app.data.repository.AuthRepository
import com.careersandbox.app.data.repository.RemoteAuthRepository
import kotlinx.coroutines.launch
import com.careersandbox.app.data.remote.RegisterRequest
import com.careersandbox.app.data.local.SessionManager
import com.careersandbox.app.data.local.UserStore

sealed interface RegisterUiState {
    data object Idle : RegisterUiState
    data object Loading : RegisterUiState
    data class Success(val userId: String) : RegisterUiState
    data class Error(val message: String) : RegisterUiState
}

class RegisterViewModel(
    private val repo: AuthRepository = RemoteAuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf<RegisterUiState>(RegisterUiState.Idle)
        private set

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            uiState = RegisterUiState.Loading
            repo.register(request)
                .onSuccess {
                    it.token?.let { t -> SessionManager.save(t, it.userId) }
                    UserStore.refresh() // new account auto-logged-in: load its profile too
                    uiState = RegisterUiState.Success(it.userId)
                }
                .onFailure { uiState = RegisterUiState.Error(it.message ?: "註冊失敗，請稍後再試") }
        }
    }
}
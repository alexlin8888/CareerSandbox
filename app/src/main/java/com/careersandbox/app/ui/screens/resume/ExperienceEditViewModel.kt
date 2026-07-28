package com.careersandbox.app.ui.screens.resume

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careersandbox.app.data.remote.CreateExperienceRequest
import com.careersandbox.app.data.repository.ExperienceRepository
import com.careersandbox.app.data.repository.RemoteExperienceRepository
import kotlinx.coroutines.launch

sealed interface SaveExperienceUiState {
    data object Idle : SaveExperienceUiState
    data object Saving : SaveExperienceUiState
    data object Success : SaveExperienceUiState
    data class Error(val message: String) : SaveExperienceUiState
}

class ExperienceEditViewModel(
    private val repo: ExperienceRepository = RemoteExperienceRepository()
) : ViewModel() {

    var uiState by mutableStateOf<SaveExperienceUiState>(SaveExperienceUiState.Idle)
        private set

    fun save(request: CreateExperienceRequest) {
        viewModelScope.launch {
            uiState = SaveExperienceUiState.Saving
            repo.create(request)
                .onSuccess { uiState = SaveExperienceUiState.Success }
                .onFailure { uiState = SaveExperienceUiState.Error(it.message ?: "儲存失敗，請稍後再試") }
        }
    }
}
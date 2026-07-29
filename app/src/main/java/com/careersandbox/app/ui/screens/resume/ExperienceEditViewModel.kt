package com.careersandbox.app.ui.screens.resume

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careersandbox.app.data.remote.CreateExperienceRequest
import com.careersandbox.app.data.remote.ExperienceResponse
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

    // Edit mode: the record fetched for prefilling; null while loading
    var loadedExperience by mutableStateOf<ExperienceResponse?>(null)
        private set

    var loadError by mutableStateOf<String?>(null)
        private set

    fun load(id: String) {
        viewModelScope.launch {
            repo.get(id)
                .onSuccess { loadedExperience = it; loadError = null }
                .onFailure { loadError = it.message }
        }
    }

    // expId == null → create; otherwise → update that record
    fun save(expId: String?, request: CreateExperienceRequest) {
        viewModelScope.launch {
            uiState = SaveExperienceUiState.Saving
            val result = if (expId == null) repo.create(request) else repo.update(expId, request)
            result
                .onSuccess { uiState = SaveExperienceUiState.Success }
                .onFailure { uiState = SaveExperienceUiState.Error(it.message ?: "儲存失敗，請稍後再試") }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            uiState = SaveExperienceUiState.Saving
            repo.delete(id)
                .onSuccess { uiState = SaveExperienceUiState.Success }
                .onFailure { uiState = SaveExperienceUiState.Error(it.message ?: "刪除失敗，請稍後再試") }
        }
    }
}
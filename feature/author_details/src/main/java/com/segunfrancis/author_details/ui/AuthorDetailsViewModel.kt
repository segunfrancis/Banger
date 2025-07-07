package com.segunfrancis.author_details.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.author_details.domain.AuthorDetailsRepository
import com.segunfrancis.author_details.domain.UserPhotos
import com.segunfrancis.remote.handleHttpExceptions
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthorDetailsViewModel(
    private val repository: AuthorDetailsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState: MutableStateFlow<AuthorDetailsUiState> =
        MutableStateFlow(AuthorDetailsUiState())
    val uiState: StateFlow<AuthorDetailsUiState> = _uiState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _uiState.update {
            it.copy(
                isLoading = false,
                photosError = throwable.handleHttpExceptions()
            )
        }
    }

    private val username: String = savedStateHandle.get<String>("username").orEmpty()

    init {
        getUserPhotos()
    }

    fun getUserPhotos() {
        viewModelScope.launch(exceptionHandler) {
            repository.getUserPhotos(username)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            photos = it,
                            photosError = null
                        )
                    }
                }
                .onFailure {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            photosError = it.handleHttpExceptions()
                        )
                    }
                }
        }
    }
}

data class AuthorDetailsUiState(
    val isLoading: Boolean = false,
    val photos: List<UserPhotos> = emptyList(),
    val photosError: String? = null
)

package com.segunfrancis.author_details.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.author_details.domain.AuthorDetailsRepository
import com.segunfrancis.author_details.domain.UserItem
import com.segunfrancis.author_details.domain.UserPhotos
import com.segunfrancis.remote.handleHttpExceptions
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthorDetailsViewModel(
    private val repository: AuthorDetailsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState: MutableStateFlow<AuthorDetailsUiState> =
        MutableStateFlow(AuthorDetailsUiState())
    val uiState: StateFlow<AuthorDetailsUiState> = _uiState.asStateFlow()

    private val _action: MutableSharedFlow<AuthorDetailsAction> = MutableSharedFlow()
    val action: SharedFlow<AuthorDetailsAction> = _action.asSharedFlow()

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
        getUserDetails()
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

    private fun getUserDetails() {
        viewModelScope.launch(exceptionHandler) {
            repository.getUserDetailsByUsername(username)
                .onSuccess { successFlow ->
                    successFlow
                        .catch { _action.emit(AuthorDetailsAction.ShowError(it.handleHttpExceptions())) }
                        .collect { userItem ->
                            _uiState.update { it.copy(userItem = userItem) }
                        }
                }
                .onFailure {
                    _action.emit(AuthorDetailsAction.ShowError(it.handleHttpExceptions()))
                }
        }
    }

    fun toggleLikeStatus() {
        viewModelScope.launch(exceptionHandler) {
            uiState.value.userItem?.let {
                repository.updateAuthorFavouriteStatus(
                    username,
                    !it.isFavourite
                )
                    .onFailure { throwable ->
                        _action.emit(AuthorDetailsAction.ShowError(throwable.handleHttpExceptions()))
                    }
            }
        }
    }
}

data class AuthorDetailsUiState(
    val isLoading: Boolean = false,
    val photos: List<UserPhotos> = emptyList(),
    val photosError: String? = null,
    val userItem: UserItem? = null
)

sealed class AuthorDetailsAction {
    data class ShowError(val errorMessage: String?) : AuthorDetailsAction()
}

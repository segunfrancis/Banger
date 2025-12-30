package com.segunfrancis.home.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.home.domain.HomePhoto
import com.segunfrancis.home.domain.HomeRepository
import com.segunfrancis.remote.PhotoOrientation
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

class CategoriesDetailsViewModel(
    private val repository: HomeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState: MutableStateFlow<CategoriesDetailsUiState> =
        MutableStateFlow(CategoriesDetailsUiState())
    val uiState: StateFlow<CategoriesDetailsUiState> = _uiState.asStateFlow()

    private val _action: MutableSharedFlow<CategoriesDetailsActions> = MutableSharedFlow()
    val action: SharedFlow<CategoriesDetailsActions> = _action.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.update { it.copy(isLoading = false) }
        if (uiState.value.homePhotos.isEmpty()) {
            _uiState.update { it.copy(errorMessage = throwable.handleHttpExceptions()) }
        } else {
            viewModelScope.launch {
                _uiState.update { it.copy(errorMessage = null) }
                _action.emit(CategoriesDetailsActions.ShowError(throwable.handleHttpExceptions()))
            }
        }
    }

    private val query = savedStateHandle.get<String>("category").orEmpty()

    init {
        getPhotos()
    }

    fun getPhotos() {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update { it.copy(isLoading = true) }
            repository.getPhotos(query = query, orientation = PhotoOrientation.Portrait)
                .catch { throwable ->
                    _uiState.update { it.copy(isLoading = false) }
                    if (uiState.value.homePhotos.isEmpty()) {
                        _uiState.update { it.copy(errorMessage = throwable.handleHttpExceptions()) }
                    } else {
                        _uiState.update { it.copy(errorMessage = null) }
                        _action.emit(CategoriesDetailsActions.ShowError(throwable.handleHttpExceptions()))
                    }
                }
                .collect { photos ->
                    _uiState.update { it.copy(isLoading = photos.isEmpty(), homePhotos = photos, errorMessage = null) }
                }
        }
    }
}

data class CategoriesDetailsUiState(
    val homePhotos: List<HomePhoto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class CategoriesDetailsActions {
    data class ShowError(val message: String?) : CategoriesDetailsActions()
}

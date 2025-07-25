package com.segunfrancis.home.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.home.domain.HomePhoto
import com.segunfrancis.home.domain.HomeRepository
import com.segunfrancis.remote.PhotoOrientation
import com.segunfrancis.remote.handleHttpExceptions
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriesDetailsViewModel(
    private val repository: HomeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState: MutableStateFlow<CategoriesDetailsUiState> =
        MutableStateFlow(CategoriesDetailsUiState.Loading)
    val uiState: StateFlow<CategoriesDetailsUiState> = _uiState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.update { CategoriesDetailsUiState.Error(throwable.handleHttpExceptions()) }
    }

    private val query = savedStateHandle.get<String>("category").orEmpty()

    init {
        getPhotos()
    }

    fun getPhotos() {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update { CategoriesDetailsUiState.Loading }
            repository.getRandomPhotos(orientation = PhotoOrientation.Portrait, query = query)
                .onSuccess { response -> _uiState.update { CategoriesDetailsUiState.Success(response.second) } }
                .onFailure { throwable -> _uiState.update { CategoriesDetailsUiState.Error(throwable.handleHttpExceptions()) } }
        }
    }
}

sealed class CategoriesDetailsUiState {
    data class Success(val homePhotos: List<HomePhoto>) : CategoriesDetailsUiState()
    data object Loading : CategoriesDetailsUiState()
    data class Error(val message: String?) : CategoriesDetailsUiState()
}

package com.segunfrancis.home.ui.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.home.ui.domain.HomeRepository
import com.segunfrancis.home.ui.domain.PhotosResponseItem
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

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _action: MutableSharedFlow<HomeActions> =
        MutableSharedFlow(onBufferOverflow = BufferOverflow.DROP_OLDEST, extraBufferCapacity = 1)
    val action: SharedFlow<HomeActions> = _action.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _action.tryEmit(HomeActions.ShowError(throwable.localizedMessage))
    }

    init {
        getPhotos()
    }

    private fun getPhotos() {
        viewModelScope.launch(exceptionHandler) {
            repository.getPhotos()
                .onSuccess { result ->
                    _uiState.update { it.copy(photos = result) }
                }
                .onFailure {
                    _action.tryEmit(HomeActions.ShowError(it.localizedMessage))
                }
        }
    }
}

data class HomeUiState(val photos: List<PhotosResponseItem> = emptyList())

sealed class HomeActions {
    data class ShowError(val errorMessage: String?) : HomeActions()
}

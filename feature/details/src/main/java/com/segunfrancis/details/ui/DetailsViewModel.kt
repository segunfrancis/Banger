package com.segunfrancis.details.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.details.domain.DetailsRepository
import com.segunfrancis.remote.PhotosResponseItem
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: DetailsRepository
) : ViewModel() {

    private val _action: MutableSharedFlow<DetailsActions> =
        MutableSharedFlow(onBufferOverflow = BufferOverflow.DROP_OLDEST, extraBufferCapacity = 1)
    val action: SharedFlow<DetailsActions> = _action.asSharedFlow()

    var uiState: MutableStateFlow<DetailsUiState> = MutableStateFlow(DetailsUiState.Loading)
        private set

    private var photosResponseItem: PhotosResponseItem? = null
    private var photoId: String = ""

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _action.tryEmit(DetailsActions.ShowMessage(throwable.localizedMessage))
    }

    init {
        savedStateHandle.get<String>("id")?.let {
            photoId = it
            getPhotoDetails(it)
        }
    }

    fun getPhotoDetails(id: String = photoId) {
        viewModelScope.launch(exceptionHandler) {
            uiState.update { DetailsUiState.Loading }
            repository.getPhotoDetails(id)
                .onSuccess { result ->
                    photosResponseItem = result
                    uiState.update { DetailsUiState.Content(result) }
                }
                .onFailure { error ->
                    uiState.update { DetailsUiState.Error(error.localizedMessage) }
                }
        }
    }

    fun downloadImage() {
        viewModelScope.launch(exceptionHandler) {
            uiState.value.run {
                photosResponseItem?.links?.downloadLocation?.let { downloadLocation ->
                    repository.downloadImage(downloadLocation)
                        .onSuccess {
                            _action.tryEmit(DetailsActions.ShowMessage("Download Location: ${it?.path}"))
                            val id = photosResponseItem?.id.orEmpty()
                            repository.trackDownload(id)
                        }
                        .onFailure {
                            _action.tryEmit(DetailsActions.ShowMessage(it.localizedMessage))
                        }
                }
            }
        }
    }
}

sealed class DetailsActions {
    data class ShowMessage(val message: String?) : DetailsActions()
}

sealed class DetailsUiState {
    data object Loading : DetailsUiState()
    data class Content(val photosResponseItem: PhotosResponseItem) : DetailsUiState()
    data class Error(val message: String?) : DetailsUiState()
}

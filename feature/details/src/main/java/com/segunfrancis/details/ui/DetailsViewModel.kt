package com.segunfrancis.details.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.details.domain.DetailsRepository
import com.segunfrancis.remote.PhotosResponseItem
import com.segunfrancis.remote.handleHttpExceptions
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

    var uiState: MutableStateFlow<DetailsUiState> = MutableStateFlow(DetailsUiState())
        private set

    private var photosResponseItem: PhotosResponseItem? = null
    private var photoId: String = ""

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _action.tryEmit(DetailsActions.ShowMessage(throwable.handleHttpExceptions()))
    }

    init {
        savedStateHandle.get<String>("id")?.let {
            photoId = it
            getPhotoDetails(it)
        }
    }

    fun getPhotoDetails(id: String = photoId) {
        viewModelScope.launch(exceptionHandler) {
            uiState.update { it.copy(isLoading = true) }
            repository.getPhotoDetails(id)
                .onSuccess { result ->
                    photosResponseItem = result
                    uiState.update { it.copy(photosResponseItem = result) }
                    checkFavourite()
                }
                .onFailure { error ->
                    uiState.update { it.copy(detailsError = error.handleHttpExceptions()) }
                }
            uiState.update { it.copy(isLoading = false) }
        }
    }

    fun downloadImage() {
        viewModelScope.launch(exceptionHandler) {
            photosResponseItem?.links?.downloadLocation?.let { downloadLocation ->
                repository.downloadImage(downloadLocation)
                    .onSuccess {
                        _action.tryEmit(DetailsActions.ShowMessage("Download Location: ${it?.path}"))
                        val id = photosResponseItem?.id.orEmpty()
                        repository.trackDownload(id)
                    }
                    .onFailure {
                        _action.tryEmit(DetailsActions.ShowMessage(it.handleHttpExceptions()))
                    }
            }
        }
    }

    fun togglePhotoFavourite() {
        viewModelScope.launch(exceptionHandler) {
            if (uiState.value.isFavourite) {
                photosResponseItem?.let { repository.removePhotoFromFavourite(it.id) }
            } else {
                photosResponseItem?.let { repository.addPhotoToFavourite(it) }
            }
        }
    }

    private fun checkFavourite() {
        viewModelScope.launch(exceptionHandler) {
            photosResponseItem?.let {
                repository.getPhotoById(it.id)
                    .onSuccess { photoWithUserFlow ->
                        photoWithUserFlow.collect { photoWithUser ->
                            uiState.update { state ->
                                state.copy(
                                    isFavourite = photoWithUser?.userEntity?.photoId.equals(
                                        it.id,
                                        ignoreCase = true
                                    )
                                )
                            }
                        }
                    }
                    .onFailure {
                        _action.tryEmit(DetailsActions.ShowMessage(it.handleHttpExceptions()))
                    }
            }
        }
    }
}

sealed class DetailsActions {
    data class ShowMessage(val message: String?) : DetailsActions()
}

data class DetailsUiState(
    val isLoading: Boolean = false,
    val photosResponseItem: PhotosResponseItem? = null,
    val detailsError: String? = null,
    val isFavourite: Boolean = false
)

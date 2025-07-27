package com.segunfrancis.details.ui

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.details.domain.DetailsRepository
import com.segunfrancis.details.domain.WallpaperOption
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

    private var photoId: String = ""

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        uiState.update { it.copy(isLoading = false) }
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
                    uiState.update { it.copy(photosResponseItem = result) }
                    checkFavourite()
                    uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    uiState.update { it.copy(detailsError = error.handleHttpExceptions()) }
                    uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun downloadImage() = viewModelScope.launch(exceptionHandler) {
        uiState.value.photosResponseItem?.links?.downloadLocation?.let { downloadLocation ->
            uiState.update { it.copy(isLoading = true) }
            repository.downloadImage(downloadLocation)
                .onSuccess {
                    uiState.update { state -> state.copy(imageUri = it) }
                    _action.tryEmit(DetailsActions.ShowMessage("Download Location: ${it?.path}"))
                    val id = uiState.value.photosResponseItem?.id.orEmpty()
                    repository.trackDownload(id)
                }
                .onFailure {
                    _action.tryEmit(DetailsActions.ShowMessage(it.handleHttpExceptions()))
                }
            uiState.update { it.copy(isLoading = false) }
        }
    }

    fun setWallpaper(option: WallpaperOption) {
        viewModelScope.launch(exceptionHandler) {
            if (uiState.value.imageUri != null) {
                setHomeLockWallpaper(option)
            } else {
                downloadImage().join()
                setHomeLockWallpaper(option)
            }
        }
    }

    private fun setHomeLockWallpaper(option: WallpaperOption) = viewModelScope.launch(exceptionHandler) {
        uiState.value.imageUri?.let { uri ->
            uiState.update { it.copy(isLoading = true) }
            repository.setHomeLockScreenFromUri(uri, option)
                .onSuccess {
                    _action.tryEmit(DetailsActions.ShowMessage("${option.title} updated!"))
                }
                .onFailure {
                    _action.tryEmit(DetailsActions.ShowMessage(it.handleHttpExceptions()))
                }
            uiState.update { it.copy(isLoading = false) }
        }
    }

    fun togglePhotoFavourite() {
        viewModelScope.launch(exceptionHandler) {
            if (uiState.value.isFavourite) {
                uiState.value.photosResponseItem?.let { repository.removePhotoFromFavourite(it.id) }
            } else {
                uiState.value.photosResponseItem?.let { repository.addPhotoToFavourite(it) }
            }
        }
    }

    private fun checkFavourite() {
        viewModelScope.launch(exceptionHandler) {
            uiState.value.photosResponseItem?.let {
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
                    .onFailure { throwable ->
                        _action.tryEmit(DetailsActions.ShowMessage(throwable.handleHttpExceptions()))
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
    val isFavourite: Boolean = false,
    val imageUri: Uri? = null,
    val detailsLoading: Boolean = false,
)

package com.segunfrancis.details.ui

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.details.domain.DetailPhoto
import com.segunfrancis.details.domain.DetailsRepository
import com.segunfrancis.details.domain.WallpaperOption
import com.segunfrancis.remote.handleHttpExceptions
import kotlinx.coroutines.CoroutineExceptionHandler
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

    private val _action: MutableSharedFlow<DetailsActions> = MutableSharedFlow()
    val action: SharedFlow<DetailsActions> = _action.asSharedFlow()

    var uiState: MutableStateFlow<DetailsUiState> = MutableStateFlow(DetailsUiState())
        private set

    private var photoId: String = ""

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        uiState.update { it.copy(isLoading = false) }
        viewModelScope.launch {
            _action.emit(DetailsActions.ShowMessage(throwable.handleHttpExceptions()))
        }
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
                .onSuccess { resultFlow ->
                    resultFlow.collect { photo ->
                        uiState.update { it.copy(isLoading = false, photosResponse = photo) }
                    }
                }
                .onFailure { error ->
                    uiState.update { it.copy(detailsError = error.handleHttpExceptions(), isLoading = false) }
                }
        }
    }

    fun downloadImage() = viewModelScope.launch(exceptionHandler) {
        uiState.value.photosResponse?.downloadLocation?.let { downloadLocation ->
            uiState.update { it.copy(isLoading = true) }
            repository.downloadImage(downloadLocation)
                .onSuccess {
                    uiState.update { state -> state.copy(imageUri = it) }
                    _action.emit(DetailsActions.ShowMessage("Download Location: ${it?.path}"))
                    val id = uiState.value.photosResponse?.id.orEmpty()
                    repository.trackDownload(id)
                }
                .onFailure {
                    _action.emit(DetailsActions.ShowMessage(it.handleHttpExceptions()))
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

    private fun setHomeLockWallpaper(option: WallpaperOption) =
        viewModelScope.launch(exceptionHandler) {
            uiState.value.imageUri?.let { uri ->
                uiState.update { it.copy(isLoading = true) }
                repository.setHomeLockScreenFromUri(uri, option)
                    .onSuccess {
                        _action.emit(DetailsActions.ShowMessage("${option.title} updated!"))
                    }
                    .onFailure {
                        _action.emit(DetailsActions.ShowMessage(it.handleHttpExceptions()))
                    }
                uiState.update { it.copy(isLoading = false) }
            }
        }

    fun togglePhotoFavourite() {
        viewModelScope.launch(exceptionHandler) {
            uiState.value.photosResponse?.let {
                repository.updateFavouriteStatus(
                    photoId = it.id,
                    isFavourite = !it.isFavourite
                )
            }
        }
    }
}

sealed class DetailsActions {
    data class ShowMessage(val message: String?) : DetailsActions()
}

data class DetailsUiState(
    val isLoading: Boolean = false,
    val photosResponse: DetailPhoto? = null,
    val detailsError: String? = null,
    val imageUri: Uri? = null,
    val detailsLoading: Boolean = false,
)

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

    var photoDetailsState: MutableStateFlow<PhotosResponseItem?> = MutableStateFlow(null)
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _action.tryEmit(DetailsActions.ShowMessage(throwable.localizedMessage))
    }

    init {
        savedStateHandle.get<String>("id")?.let {
            getPhotoDetails(it)
        }
    }

    private fun getPhotoDetails(id: String) {
        viewModelScope.launch(exceptionHandler) {
            repository.getPhotoDetails(id)
                .onSuccess { result ->
                    photoDetailsState.update { result }
                }
                .onFailure {
                    _action.tryEmit(DetailsActions.ShowMessage(it.localizedMessage))
                }
        }
    }

    fun downloadImage() {
        viewModelScope.launch(exceptionHandler) {
            photoDetailsState.value?.links?.downloadLocation?.let { downloadLocation ->
                repository.downloadImage(downloadLocation)
                    .onSuccess {
                        _action.tryEmit(DetailsActions.ShowMessage("Download Location: ${it?.path}"))
                        val id = photoDetailsState.value?.id.orEmpty()
                        repository.trackDownload(id)
                    }
                    .onFailure {
                        _action.tryEmit(DetailsActions.ShowMessage(it.localizedMessage))
                    }
            }
        }
    }
}

sealed class DetailsActions {
    data class ShowMessage(val message: String?) : DetailsActions()
}

package com.segunfrancis.home.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.home.domain.HomePhoto
import com.segunfrancis.home.domain.HomeRepositoryImpl
import com.segunfrancis.home.domain.HomeUseCase
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

class HomeViewModel(private val useCase: HomeUseCase) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _action: MutableSharedFlow<HomeActions> =
        MutableSharedFlow(onBufferOverflow = BufferOverflow.DROP_OLDEST, extraBufferCapacity = 1)
    val action: SharedFlow<HomeActions> = _action.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.update { it.copy(isLoading = false) }
        _action.tryEmit(HomeActions.ShowError(throwable.handleHttpExceptions()))
    }

    init {
        getHomePhotos()
    }

    private fun getHomePhotos() {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update { it.copy(isLoading = true) }
            val response = mutableSetOf<Pair<String, List<PhotoItem>>>()
            useCase.invoke().forEach { result ->
                result.onSuccess { success ->
                    response.add(success.toPhotoItemPair())
                    _uiState.update { it.copy(isLoading = false, homePhotos = response.toList()) }
                }.onFailure { throwable ->
                    if (throwable is HomeRepositoryImpl.QueryAwareThrowable) {
                        // Todo: Update UI accordingly
                    } else {
                        _action.tryEmit(HomeActions.ShowError(throwable.handleHttpExceptions()))
                    }
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }
}

data class HomeUiState(
    val homePhotos: List<Pair<String, List<PhotoItem>>> = emptyList(),
    val isLoading: Boolean = false
)

sealed class HomeActions {
    data class ShowError(val errorMessage: String?) : HomeActions()
}

data class PhotoItem(
    val id: String,
    val thumb: String,
    val blurHash: String,
    val description: String?,
    val altDescription: String?,
    val blurHashBitmap: Bitmap?
)

fun HomePhoto.toPhotoItem(): PhotoItem {
    return with(this) {
        PhotoItem(
            id = id,
            thumb = thumb,
            blurHash = blurHash,
            description = description,
            altDescription = altDescription,
            blurHashBitmap = blurHashBitmap
        )
    }
}

fun Pair<String, List<HomePhoto>>.toPhotoItemPair(): Pair<String, List<PhotoItem>> {
    return this.first to this.second.map { it.toPhotoItem() }
}

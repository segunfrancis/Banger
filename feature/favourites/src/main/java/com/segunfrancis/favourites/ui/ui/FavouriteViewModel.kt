package com.segunfrancis.favourites.ui.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.favourites.ui.domain.FavouriteRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn

class FavouriteViewModel(private val repository: FavouriteRepository) : ViewModel() {

    private val _action: MutableSharedFlow<FavouriteAction> =
        MutableSharedFlow(onBufferOverflow = BufferOverflow.DROP_OLDEST, extraBufferCapacity = 1)
    val action: SharedFlow<FavouriteAction> = _action.asSharedFlow()

    val favouritePhotos = repository.getFavourites().catch { throwable ->
        throwable.printStackTrace()
        throwable.localizedMessage?.let {
            _action.tryEmit(FavouriteAction.ShowError(it))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    sealed interface FavouriteAction {
        data class ShowError(val errorMessage: String) : FavouriteAction
    }
}

package com.segunfrancis.author.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.author.domain.AuthorItem
import com.segunfrancis.author.domain.AuthorRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class AuthorViewModel(private val repository: AuthorRepository) : ViewModel() {

    private val _favouriteAuthorsState = MutableStateFlow<List<AuthorItem>>(emptyList())
    val favouriteAuthorsState: StateFlow<List<AuthorItem>> = _favouriteAuthorsState.asStateFlow()

    private val _action = MutableSharedFlow<AuthorActions>()
    val action: SharedFlow<AuthorActions> = _action.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        viewModelScope.launch {
            _action.emit(AuthorActions.ShowError(throwable.localizedMessage))
        }
    }

    init {
        getFavouriteAuthors()
    }

    private fun getFavouriteAuthors() {
        viewModelScope.launch(exceptionHandler) {
            repository.getFavouriteAuthors()
                .onSuccess { favAuthorsFlow ->
                    favAuthorsFlow.collect { authors ->
                        _favouriteAuthorsState.update { authors }
                    }
                }
                .onFailure {
                    it.printStackTrace()
                    _action.emit(AuthorActions.ShowError(it.localizedMessage))
                }
        }
    }
}

sealed class AuthorActions {
    data class ShowError(val errorMessage: String?): AuthorActions()
}

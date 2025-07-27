package com.segunfrancis.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.local.AppTheme
import com.segunfrancis.local.DownloadQuality
import com.segunfrancis.local.SettingsRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    val theme = repository.getTheme()
        .catch { it.printStackTrace() }
        .stateIn(viewModelScope, SharingStarted.Lazily, AppTheme.System)

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch(exceptionHandler) {
            repository.setTheme(theme)
        }
    }

    val downloadQuality = repository.getDownloadQuality()
        .catch { it.printStackTrace() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DownloadQuality.High)

    fun setDownloadQuality(quality: DownloadQuality) {
        viewModelScope.launch(exceptionHandler) {
            repository.setDownloadQuality(quality)
        }
    }
}

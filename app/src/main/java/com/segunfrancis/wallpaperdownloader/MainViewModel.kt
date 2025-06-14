package com.segunfrancis.wallpaperdownloader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.local.AppTheme
import com.segunfrancis.local.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class MainViewModel(repository: SettingsRepository) : ViewModel() {
    val theme = repository.getTheme()
        .catch { it.printStackTrace() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), AppTheme.SystemDefault)
}

val mainModule = module {
    viewModelOf(::MainViewModel)
}

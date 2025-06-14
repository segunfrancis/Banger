package com.segunfrancis.settings.di

import com.segunfrancis.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule = module {
    viewModelOf(::SettingsViewModel)
}

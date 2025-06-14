package com.segunfrancis.local

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val localModule = module {
    single<WDDao> {
        WDDatabase.getDatabase(context = androidApplication().applicationContext).getDao()
    }

    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
            produceFile = {
                androidApplication().applicationContext.preferencesDataStoreFile("app-theme-preference")
            },
            scope = CoroutineScope(Dispatchers.IO)
        )
    }

    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class
}

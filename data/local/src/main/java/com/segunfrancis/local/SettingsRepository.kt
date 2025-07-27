package com.segunfrancis.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.segunfrancis.local.PreferenceKeys.DOWNLOAD_QUALITY_PREFS_KEY
import com.segunfrancis.local.PreferenceKeys.THEME_PREFS_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SettingsRepository {
    fun getTheme(): Flow<AppTheme>
    suspend fun setTheme(theme: AppTheme)
    fun getDownloadQuality(): Flow<DownloadQuality>
    suspend fun setDownloadQuality(quality: DownloadQuality)
}

class SettingsRepositoryImpl(private val datastore: DataStore<Preferences>) : SettingsRepository {
    override fun getTheme(): Flow<AppTheme> {
        return datastore.data.map { preferences ->
            AppTheme.valueOf(preferences[THEME_PREFS_KEY] ?: AppTheme.System.name)
        }
    }

    override suspend fun setTheme(theme: AppTheme) {
        datastore.edit { preferences ->
            preferences[THEME_PREFS_KEY] = theme.name
        }
    }

    override fun getDownloadQuality(): Flow<DownloadQuality> {
        return datastore.data.map { preferences ->
            DownloadQuality.valueOf(preferences[DOWNLOAD_QUALITY_PREFS_KEY] ?: DownloadQuality.High.name)
        }
    }

    override suspend fun setDownloadQuality(quality: DownloadQuality) {
        datastore.edit { preferences ->
            preferences[DOWNLOAD_QUALITY_PREFS_KEY] = quality.name
        }
    }
}

private object PreferenceKeys {
    val THEME_PREFS_KEY = stringPreferencesKey("theme_prefs_keys")
    val DOWNLOAD_QUALITY_PREFS_KEY = stringPreferencesKey("download_quality_prefs_keys")
}

enum class AppTheme {
    Light, Dark, System
}

enum class DownloadQuality {
    High, Medium, Low
}

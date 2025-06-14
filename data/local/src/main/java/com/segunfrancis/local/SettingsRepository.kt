package com.segunfrancis.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.segunfrancis.local.PreferenceKeys.THEME_PREFS_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SettingsRepository {
    fun getTheme(): Flow<AppTheme>
    suspend fun setTheme(theme: AppTheme)
}

class SettingsRepositoryImpl(private val datastore: DataStore<Preferences>) : SettingsRepository {
    override fun getTheme(): Flow<AppTheme> {
        return datastore.data.map { preferences ->
            AppTheme.valueOf(preferences[THEME_PREFS_KEY] ?: AppTheme.SystemDefault.name)
        }
    }

    override suspend fun setTheme(theme: AppTheme) {
        datastore.edit { preferences ->
            preferences[THEME_PREFS_KEY] = theme.name
        }
    }
}

private object PreferenceKeys {
    val THEME_PREFS_KEY = stringPreferencesKey("theme_prefs_keys")
}

enum class AppTheme(val value: String) {
    Light("Light"), Dark("Dark"), SystemDefault("System Default")
}

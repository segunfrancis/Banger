package com.segunfrancis.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepositoryImplTest {

    @Test
    fun `setTheme persists and getTheme reads latest value`() = runTest {
        val dataStore = InMemoryPreferencesDataStore()
        val repository = SettingsRepositoryImpl(dataStore)

        repository.setTheme(AppTheme.Dark)

        assertThat(repository.getTheme().first()).isEqualTo(AppTheme.Dark)
    }

    @Test
    fun `setDownloadQuality persists and getDownloadQuality reads latest value`() = runTest {
        val dataStore = InMemoryPreferencesDataStore()
        val repository = SettingsRepositoryImpl(dataStore)

        repository.setDownloadQuality(DownloadQuality.Medium)

        assertThat(repository.getDownloadQuality().first()).isEqualTo(DownloadQuality.Medium)
    }

    private class InMemoryPreferencesDataStore : DataStore<Preferences> {
        private val state = MutableStateFlow<Preferences>(emptyPreferences())

        override val data: Flow<Preferences> = state

        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            val updated = transform(state.value)
            state.value = updated
            return updated
        }
    }
}

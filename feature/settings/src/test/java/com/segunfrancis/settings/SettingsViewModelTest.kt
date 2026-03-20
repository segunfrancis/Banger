package com.segunfrancis.settings

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.segunfrancis.local.AppTheme
import com.segunfrancis.local.DownloadQuality
import com.segunfrancis.local.SettingsRepository
import com.segunfrancis.settings.ui.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setTheme delegates to repository`() = runTest {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)

        viewModel.setTheme(AppTheme.Dark)
        advanceUntilIdle()

        assertThat(repository.lastTheme).isEqualTo(AppTheme.Dark)
    }

    @Test
    fun `downloadQuality emits repository values`() = runTest {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)

        viewModel.downloadQuality.test {
            assertThat(awaitItem()).isEqualTo(DownloadQuality.High)
            repository.qualityFlow.value = DownloadQuality.Low
            assertThat(awaitItem()).isEqualTo(DownloadQuality.Low)
        }
    }

    private class FakeSettingsRepository : SettingsRepository {
        val themeFlow = MutableStateFlow(AppTheme.System)
        val qualityFlow = MutableStateFlow(DownloadQuality.High)
        var lastTheme: AppTheme = AppTheme.System

        override fun getTheme(): Flow<AppTheme> = themeFlow

        override suspend fun setTheme(theme: AppTheme) {
            lastTheme = theme
            themeFlow.value = theme
        }

        override fun getDownloadQuality(): Flow<DownloadQuality> = qualityFlow

        override suspend fun setDownloadQuality(quality: DownloadQuality) {
            qualityFlow.value = quality
        }
    }
}

package com.segunfrancis.wallpaperdownloader

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.segunfrancis.local.AppTheme
import com.segunfrancis.local.DownloadQuality
import com.segunfrancis.local.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `theme follows repository flow`() = runTest {
        val repository = object : SettingsRepository {
            private val theme = MutableStateFlow(AppTheme.System)
            override fun getTheme(): Flow<AppTheme> = theme
            override suspend fun setTheme(theme: AppTheme) {
                this.theme.value = theme
            }
            override fun getDownloadQuality(): Flow<DownloadQuality> = MutableStateFlow(DownloadQuality.High)
            override suspend fun setDownloadQuality(quality: DownloadQuality) = Unit
        }

        val viewModel = MainViewModel(repository)

        viewModel.theme.test {
            assertThat(awaitItem()).isEqualTo(AppTheme.System)
        }
    }
}

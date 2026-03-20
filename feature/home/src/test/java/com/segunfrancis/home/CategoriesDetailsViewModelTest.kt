package com.segunfrancis.home

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.segunfrancis.home.domain.HomePhoto
import com.segunfrancis.home.domain.HomeRepository
import com.segunfrancis.home.ui.CategoriesDetailsActions
import com.segunfrancis.home.ui.CategoriesDetailsViewModel
import com.segunfrancis.remote.PhotoOrientation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesDetailsViewModelTest {

    private val dispatcher: TestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getPhotos updates uiState with photos`() = runTest {
        val photos = listOf(samplePhoto("1"), samplePhoto("2"))
        val repository = FakeHomeRepository(flow { emit(photos) })
        val viewModel = CategoriesDetailsViewModel(
            repository = repository,
            savedStateHandle = SavedStateHandle(mapOf("category" to "nature"))
        )

        advanceUntilIdle()

        assertThat(viewModel.uiState.value.homePhotos).hasSize(2)
        assertThat(viewModel.uiState.value.errorMessage).isNull()
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun `getPhotos emits action on refresh failure when existing data exists`() = runTest {
        val flow = MutableSharedFlow<List<HomePhoto>>()
        val repository = FakeHomeRepository(flow)
        val viewModel = CategoriesDetailsViewModel(
            repository = repository,
            savedStateHandle = SavedStateHandle(mapOf("category" to "nature"))
        )

        viewModel.action.test {
            flow.emit(listOf(samplePhoto("1")))
            advanceUntilIdle()

            flow.emit(emptyList())
            repository.emitError(Throwable("network unavailable"))
            viewModel.getPhotos()
            advanceUntilIdle()

            val event = awaitItem() as CategoriesDetailsActions.ShowError
            assertThat(event.message).contains("network unavailable")
        }
    }

    private class FakeHomeRepository(private val photosFlow: Flow<List<HomePhoto>>) : HomeRepository {
        private var forceFailure: Throwable? = null

        fun emitError(throwable: Throwable) {
            forceFailure = throwable
        }

        override suspend fun getPhotos(): Result<List<HomePhoto>> = Result.success(emptyList())

        override suspend fun getRandomPhotos(
            orientation: PhotoOrientation,
            query: String
        ): Result<Pair<String, List<HomePhoto>>> = Result.success(query to emptyList())

        override suspend fun getPhotos(
            query: String,
            orientation: PhotoOrientation
        ): Flow<List<HomePhoto>> {
            forceFailure?.let { throwable ->
                forceFailure = null
                return flow { throw throwable }
            }
            return photosFlow
        }
    }

    private fun samplePhoto(id: String) = HomePhoto(
        id = id,
        description = "description",
        blurHash = "LKO2?U%2Tw=w]~RBVZRi};RPxuwH",
        thumb = "thumb",
        blurHashBitmap = null,
        altDescription = "alt",
        height = 100,
        width = 100,
        likes = 1
    )
}

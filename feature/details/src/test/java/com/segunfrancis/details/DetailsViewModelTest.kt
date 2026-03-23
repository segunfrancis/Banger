package com.segunfrancis.details

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.segunfrancis.details.domain.DetailPhoto
import com.segunfrancis.details.domain.DetailsRepository
import com.segunfrancis.details.domain.WallpaperOption
import com.segunfrancis.details.ui.DetailsActions
import com.segunfrancis.details.ui.DetailsViewModel
import com.segunfrancis.remote.DownloadResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

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
    fun `init loads photo details`() = runTest {
        val repository = FakeDetailsRepository()
        val viewModel = DetailsViewModel(SavedStateHandle(mapOf("id" to "photo-1")), repository)

        advanceUntilIdle()

        assertThat(viewModel.uiState.value.photosResponse?.id).isEqualTo("photo-1")
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun `downloadImage emits success message`() = runTest {
        val repository = FakeDetailsRepository()
        val viewModel = DetailsViewModel(SavedStateHandle(mapOf("id" to "photo-1")), repository)

        viewModel.action.test {
            advanceUntilIdle()
            viewModel.downloadImage()
            advanceUntilIdle()

            val action = awaitItem() as DetailsActions.ShowMessage
            assertThat(action.message).contains("Download Location")
        }
    }

    private class FakeDetailsRepository : DetailsRepository {
        override suspend fun getPhotoDetails(id: String): Result<Flow<DetailPhoto?>> {
            return Result.success(flowOf(createSampleDetailPhoto(id)))
        }

        override suspend fun trackDownload(id: String): Result<DownloadResponse> {
            return Result.success(DownloadResponse("download-url"))
        }

        override suspend fun downloadImage(url: String): Result<Uri?> {
            return Result.success(null)
        }

        override suspend fun setHomeLockScreenFromUri(
            imageUri: Uri,
            option: WallpaperOption
        ): Result<Unit> = Result.success(Unit)

        override suspend fun updateFavouriteStatus(
            photoId: String,
            isFavourite: Boolean
        ): Result<Unit> {
            return Result.success(Unit)
        }
    }

    companion object {
        private fun createSampleDetailPhoto(id: String) = DetailPhoto(
            id = id,
            description = "description",
            blurHash = null,
            thumb = "thumb",
            blurHashBitmap = null,
            altDescription = "alt",
            height = 100,
            width = 100,
            likes = 1,
            isFavourite = false,
            profileImage = "profile",
            username = "john",
            name = "John",
            photoUrl = "photo",
            bio = "bio",
            downloadLocation = "loc"
        )
    }
}

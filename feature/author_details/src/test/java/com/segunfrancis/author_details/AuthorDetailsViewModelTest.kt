package com.segunfrancis.author_details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.segunfrancis.author_details.domain.AuthorDetailsRepository
import com.segunfrancis.author_details.domain.UserItem
import com.segunfrancis.author_details.domain.UserPhotos
import com.segunfrancis.author_details.ui.AuthorDetailsAction
import com.segunfrancis.author_details.ui.AuthorDetailsViewModel
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
class AuthorDetailsViewModelTest {

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
    fun `init loads photos and user details`() = runTest {
        val repository = FakeAuthorDetailsRepository()
        val viewModel = AuthorDetailsViewModel(
            repository,
            SavedStateHandle(mapOf("username" to "john"))
        )

        advanceUntilIdle()

        assertThat(viewModel.uiState.value.photos).hasSize(1)
        assertThat(viewModel.uiState.value.userItem?.username).isEqualTo("john")
    }

    @Test
    fun `onImageClick emits OnImageSaved action`() = runTest {
        val repository = FakeAuthorDetailsRepository()
        val viewModel = AuthorDetailsViewModel(repository, SavedStateHandle(mapOf("username" to "john")))

        viewModel.action.test {
            viewModel.onImageClick("image-1")
            advanceUntilIdle()

            val action = awaitItem() as AuthorDetailsAction.OnImageSaved
            assertThat(action.imageId).isEqualTo("image-1")
        }
    }

    private class FakeAuthorDetailsRepository : AuthorDetailsRepository {
        override suspend fun getUserPhotos(username: String): Result<List<UserPhotos>> {
            return Result.success(listOf(UserPhotos("image-1", 10, 10, "thumb", null)))
        }

        override suspend fun getUserDetailsByUsername(username: String): Result<Flow<UserItem?>> {
            return Result.success(flowOf(UserItem(
                id = "id",
                bio = "bio",
                name = "John",
                username = username,
                firstName = "John",
                lastName = "Doe",
                portfolioUrl = null,
                profilePhoto = "profile",
                isFavourite = false
            )))
        }

        override suspend fun updateAuthorFavouriteStatus(username: String, isFavourite: Boolean): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun saveImageDetails(imageId: String): Result<Unit> = Result.success(Unit)
    }
}

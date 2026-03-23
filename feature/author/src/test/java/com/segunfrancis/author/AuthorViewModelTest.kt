package com.segunfrancis.author

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.segunfrancis.author.domain.AuthorItem
import com.segunfrancis.author.domain.AuthorRepository
import com.segunfrancis.author.ui.AuthorActions
import com.segunfrancis.author.ui.AuthorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthorViewModelTest {

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
    fun `init loads favourite authors`() = runTest {
        val repository = FakeAuthorRepository(
            Result.success(flow { emit(listOf(sampleAuthor())) })
        )

        val viewModel = AuthorViewModel(repository)
        advanceUntilIdle()

        assertThat(viewModel.favouriteAuthorsState.value).hasSize(1)
        assertThat(viewModel.favouriteAuthorsState.value.first().name).isEqualTo("Jane Doe")
    }

    @Test
    fun `init emits error action when repository fails`() = runTest {
        val repository = FakeAuthorRepository(Result.failure(Throwable("db unavailable")))
        val viewModel = AuthorViewModel(repository)

        viewModel.action.test {
            advanceUntilIdle()
            val action = awaitItem() as AuthorActions.ShowError
            assertThat(action.errorMessage).contains("db unavailable")
        }
    }

    private class FakeAuthorRepository(
        private val result: Result<Flow<List<AuthorItem>>>
    ) : AuthorRepository {
        override suspend fun getFavouriteAuthors(): Result<Flow<List<AuthorItem>>> = result
    }

    private fun sampleAuthor() = AuthorItem(
        bio = "bio",
        firstName = "Jane",
        id = "author-1",
        lastName = "Doe",
        name = "Jane Doe",
        portfolioUrl = "https://example.com",
        username = "jane",
        isFavourite = true,
        profilePhoto = "https://example.com/photo.jpg"
    )
}

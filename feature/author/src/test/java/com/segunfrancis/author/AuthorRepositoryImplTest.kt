package com.segunfrancis.author

import com.google.common.truth.Truth.assertThat
import com.segunfrancis.author.domain.AuthorRepositoryImpl
import com.segunfrancis.local.WDDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthorRepositoryImplTest {

    @Test
    fun `getFavouriteAuthors maps dao response`() = runTest {
        val dao = FakeWDDao(favouriteAuthorsCount = 2)
        val repository = AuthorRepositoryImpl(dao = dao, dispatcher = StandardTestDispatcher(testScheduler))

        val result = repository.getFavouriteAuthors()

        val authors = result.getOrThrow().first()
        assertThat(authors).hasSize(2)
        assertThat(authors.first().isFavourite).isTrue()
    }

    private class FakeWDDao(private val favouriteAuthorsCount: Int) : WDDao by EmptyWDDao() {
        override fun getFavouriteAuthors() = flowOf(List(favouriteAuthorsCount) { index ->
            TestData.userWithProfileImage("user-$index", true)
        })
    }
}

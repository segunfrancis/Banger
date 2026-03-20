package com.segunfrancis.favourites

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.segunfrancis.favourites.ui.domain.FavouritePhotoItem
import com.segunfrancis.favourites.ui.domain.FavouritePhotoUrls
import com.segunfrancis.favourites.ui.domain.FavouriteRepository
import com.segunfrancis.favourites.ui.ui.FavouriteViewModel
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
class FavouriteViewModelTest {

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
    fun `favouritePhotos emits repository values`() = runTest {
        val repository = object : FavouriteRepository {
            override fun getFavourites(): Flow<List<FavouritePhotoItem>> = flow {
                emit(listOf(sampleFavourite("1"), sampleFavourite("2")))
            }
        }
        val viewModel = FavouriteViewModel(repository)

        viewModel.favouritePhotos.test {
            assertThat(awaitItem()).isEmpty()
            assertThat(awaitItem()).hasSize(2)
        }
    }

    private fun sampleFavourite(id: String) = FavouritePhotoItem(
        id = id,
        description = "description",
        altDescription = "alt",
        blurHashBitmap = null,
        height = 10,
        width = 10,
        likes = 1,
        urls = FavouritePhotoUrls(id, "full", "raw", "regular", "small", "thumb")
    )
}

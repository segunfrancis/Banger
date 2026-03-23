package com.segunfrancis.home

import com.google.common.truth.Truth.assertThat
import com.segunfrancis.home.domain.HomePhoto
import com.segunfrancis.home.domain.HomeRepository
import com.segunfrancis.home.domain.HomeUseCase
import com.segunfrancis.remote.PhotoOrientation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeUseCaseTest {

    @Test
    fun `invoke requests expected categories`() = runTest {
        val fakeRepository = RecordingHomeRepository()
        val useCase = HomeUseCase(fakeRepository, StandardTestDispatcher(testScheduler))

        val result = useCase()

        assertThat(result).hasSize(3)
        assertThat(fakeRepository.queries).containsExactly("popular", "nature", "random")
    }

    private class RecordingHomeRepository : HomeRepository {
        val queries = mutableListOf<String>()

        override suspend fun getPhotos(): Result<List<HomePhoto>> = Result.success(emptyList())

        override suspend fun getRandomPhotos(
            orientation: PhotoOrientation,
            query: String
        ): Result<Pair<String, List<HomePhoto>>> {
            queries += query
            return Result.success(query to emptyList())
        }

        override suspend fun getPhotos(
            query: String,
            orientation: PhotoOrientation
        ): Flow<List<HomePhoto>> = emptyFlow()
    }
}

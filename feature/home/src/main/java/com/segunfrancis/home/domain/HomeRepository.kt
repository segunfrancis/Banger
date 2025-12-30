package com.segunfrancis.home.domain

import com.segunfrancis.remote.PhotoOrientation
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    suspend fun getPhotos(): Result<List<HomePhoto>>

    suspend fun getRandomPhotos(
        orientation: PhotoOrientation,
        query: String
    ): Result<Pair<String, List<HomePhoto>>>

    suspend fun getPhotos(
        query: String,
        orientation: PhotoOrientation
    ): Flow<List<HomePhoto>>
}

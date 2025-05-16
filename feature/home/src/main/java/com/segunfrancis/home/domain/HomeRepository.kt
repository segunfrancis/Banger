package com.segunfrancis.home.domain

import com.segunfrancis.remote.PhotoOrientation

interface HomeRepository {

    suspend fun getPhotos(): Result<List<HomePhoto>>

    suspend fun getRandomPhotos(orientation: PhotoOrientation, query: String): Result<Pair<String, List<HomePhoto>>>
}

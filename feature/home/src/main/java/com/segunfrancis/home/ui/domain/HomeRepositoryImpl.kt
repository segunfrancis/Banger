package com.segunfrancis.home.ui.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class HomeRepositoryImpl(private val dispatcher: CoroutineDispatcher, private val api: HomeApi) :
    HomeRepository {
    override suspend fun getPhotos(): Result<List<PhotosResponseItem>> {
        return try {
            val photos = withContext(dispatcher) { api.getPhotos() }
            Result.success(photos)
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
    }
}

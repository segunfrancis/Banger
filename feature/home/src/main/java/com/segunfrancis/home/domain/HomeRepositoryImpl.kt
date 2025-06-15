package com.segunfrancis.home.domain

import com.segunfrancis.remote.PhotoOrientation
import com.segunfrancis.remote.PhotosResponseItem
import com.segunfrancis.utility.BlurHashDecoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class HomeRepositoryImpl(private val dispatcher: CoroutineDispatcher, private val api: HomeApi) :
    HomeRepository {
    override suspend fun getPhotos(): Result<List<HomePhoto>> {
        return try {
            val photos = withContext(dispatcher) { api.getPhotos().map { it.toHomePhoto() } }
            Result.success(photos)
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
    }

    override suspend fun getRandomPhotos(
        orientation: PhotoOrientation,
        query: String
    ): Result<Pair<String, List<HomePhoto>>> {
        return try {
            val photos = withContext(dispatcher) {
                val photos = api.getRandomPhotos(orientation = orientation.name.lowercase(), query = query)
                query to photos.map { photo -> photo.toHomePhoto() }
            }
            Result.success(photos)
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(QueryAwareThrowable(t, query))
        }
    }

    private fun PhotosResponseItem.toHomePhoto(): HomePhoto {
        return with(this) {
            HomePhoto(
                id = id,
                description = description,
                altDescription = altDescription,
                height = height,
                width = width,
                blurHash = blurHash,
                thumb = urls.thumb,
                assetType = assetType,
                color = color,
                createdAt = createdAt,
                likes = likes,
                likedByUser = likedByUser,
                slug = slug,
                updatedAt = updatedAt,
                blurHashBitmap = BlurHashDecoder.decode(
                    blurHash = blurHash,
                    width = width.div(100),
                    height = height.div(100)
                )
            )
        }
    }

    class QueryAwareThrowable(cause: Throwable, query: String) :
        Throwable(message = "Failed for query: $query", cause = cause)
}

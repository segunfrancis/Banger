package com.segunfrancis.author_details.domain

import android.graphics.Bitmap
import com.segunfrancis.author_details.domain.data.AuthorDetailsApi
import com.segunfrancis.remote.UserPhotosResponse
import com.segunfrancis.utility.BlurHashDecoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface AuthorDetailsRepository {

    suspend fun getUserPhotos(username: String): Result<List<UserPhotos>>
}

class AuthorDetailsRepositoryImpl(
    private val api: AuthorDetailsApi,
    private val dispatcher: CoroutineDispatcher
) :
    AuthorDetailsRepository {
    override suspend fun getUserPhotos(username: String): Result<List<UserPhotos>> {
        return try {
            withContext(dispatcher) {
                Result.success(api.getUserPhotos(username).map { it.toUserPhotos() })
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
    }
}

data class UserPhotos(
    val id: String,
    val width: Int,
    val height: Int,
    val photo: String,
    val blurHashBitmap: Bitmap?
)

fun UserPhotosResponse.toUserPhotos(): UserPhotos {
    return with(this) {
        UserPhotos(
            id = id,
            width = width,
            height = height,
            photo = urls.thumb,
            blurHashBitmap = BlurHashDecoder.decode(
                blurHash = blurHash,
                width = width.div(100),
                height = height.div(100)
            )
        )
    }
}

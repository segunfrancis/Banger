package com.segunfrancis.author_details.domain

import android.graphics.Bitmap
import com.segunfrancis.author_details.domain.data.AuthorDetailsApi
import com.segunfrancis.local.UserWithProfileImage
import com.segunfrancis.local.WDDao
import com.segunfrancis.remote.UserPhotosResponse
import com.segunfrancis.utility.BlurHashDecoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface AuthorDetailsRepository {

    suspend fun getUserPhotos(username: String): Result<List<UserPhotos>>

    suspend fun getUserDetailsByUsername(username: String): Result<Flow<UserItem>>

    suspend fun updateAuthorFavouriteStatus(username: String, isFavourite: Boolean): Result<Unit>
}

class AuthorDetailsRepositoryImpl(
    private val api: AuthorDetailsApi,
    private val dispatcher: CoroutineDispatcher,
    private val dao: WDDao
) : AuthorDetailsRepository {
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

    override suspend fun getUserDetailsByUsername(username: String): Result<Flow<UserItem>> {
        return try {
            Result.success(
                dao.getAuthorDetailsByUsername(username)
                    .map { it.toUserItem() }
                    .flowOn(dispatcher)
            )
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun updateAuthorFavouriteStatus(
        username: String,
        isFavourite: Boolean
    ): Result<Unit> {
        return try {
            withContext(dispatcher) {
                Result.success(dao.updateAuthorFavouriteStatus(username, isFavourite))
            }
        } catch (t: Throwable) {
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

data class UserItem(
    val id: String,
    val bio: String?,
    val name: String,
    val username: String,
    val firstName: String,
    val lastName: String?,
    val portfolioUrl: String?,
    val profilePhoto: String,
    val isFavourite: Boolean = false
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

fun UserWithProfileImage.toUserItem(): UserItem {
    return with(this) {
        UserItem(
            id = userEntity.id,
            bio = userEntity.bio,
            name = userEntity.name,
            username = userEntity.username,
            firstName = userEntity.firstName,
            lastName = userEntity.lastName,
            isFavourite = userEntity.isFavourite,
            portfolioUrl = userEntity.portfolioUrl,
            profilePhoto = userProfileImageEntity?.large.orEmpty()
        )
    }
}

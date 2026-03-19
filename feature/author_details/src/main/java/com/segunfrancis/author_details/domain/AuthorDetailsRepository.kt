package com.segunfrancis.author_details.domain

import android.graphics.Bitmap
import android.util.Log
import com.segunfrancis.author_details.domain.data.AuthorDetailsApi
import com.segunfrancis.local.LinksEntity
import com.segunfrancis.local.PhotosResponseEntity
import com.segunfrancis.local.UrlsEntity
import com.segunfrancis.local.UserEntity
import com.segunfrancis.local.UserProfileImageEntity
import com.segunfrancis.local.UserWithProfileImage
import com.segunfrancis.local.WDDao
import com.segunfrancis.remote.Links
import com.segunfrancis.remote.Urls
import com.segunfrancis.remote.User
import com.segunfrancis.remote.UserPhotosResponse
import com.segunfrancis.utility.BlurHashDecoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface AuthorDetailsRepository {

    suspend fun getUserPhotos(username: String): Result<List<UserPhotos>>

    suspend fun getUserDetailsByUsername(username: String): Result<Flow<UserItem?>>

    suspend fun updateAuthorFavouriteStatus(username: String, isFavourite: Boolean): Result<Unit>

    suspend fun saveImageDetails(imageId: String): Result<Unit>
}

class AuthorDetailsRepositoryImpl(
    private val api: AuthorDetailsApi,
    private val dispatcher: CoroutineDispatcher,
    private val dao: WDDao
) : AuthorDetailsRepository {
    private var photos: List<UserPhotosResponse> = emptyList()

    override suspend fun getUserPhotos(username: String): Result<List<UserPhotos>> {
        return try {
            withContext(dispatcher) {
                photos = api.getUserPhotos(username)
                Result.success(photos.map { it.toUserPhotos() })
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
    }

    override suspend fun getUserDetailsByUsername(username: String): Result<Flow<UserItem?>> {
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

    override suspend fun saveImageDetails(imageId: String): Result<Unit> {
        if (dao.getPhotoById(imageId).firstOrNull() != null) { // image already exists in DB
            return Result.success(Unit)
        }
        val photo = photos.find { it.id == imageId }
        Log.d("saveImageDetails", "Photo: $photo")
        if (photo == null) {
            return Result.failure(Throwable("No photo with such ID exists"))
        }
        Log.d("saveImageDetails", "UserWithProfileImage: ${photo.user.toUserWithProfileImage(photo.id)}")
        return try {
            Result.success(
                dao.insertPhoto(
                    photosResponseEntity = photo.toPhotoEntity(),
                    urlsEntity = photo.urls.toUrlsEntity(photo.id),
                    linksEntity = photo.links.toLinksEntity(photo.id),
                    userWithProfileImage = photo.user.toUserWithProfileImage(photo.id)
                )
            )
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

private fun UserPhotosResponse.toUserPhotos(): UserPhotos {
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

private fun UserPhotosResponse.toPhotoEntity(): PhotosResponseEntity {
    return PhotosResponseEntity(
        id = id,
        description = description,
        altDescription = altDescription,
        blurHash = blurHash,
        height = height,
        width = width,
        likes = likes,
        category = ""
    )
}

private fun Urls.toUrlsEntity(id: String): UrlsEntity {
    return UrlsEntity(
        photoId = id,
        full = full,
        raw = raw,
        regular = regular,
        small = small,
        thumb = thumb
    )
}

private fun Links.toLinksEntity(id: String): LinksEntity {
    return LinksEntity(
        photoId = id,
        download = download,
        downloadLocation = downloadLocation
    )
}

private fun User.toUserWithProfileImage(photoId: String): UserWithProfileImage {
    return UserWithProfileImage(
        userEntity = UserEntity(
            photoId = photoId,
            bio = bio,
            name = name,
            firstName = firstName,
            lastName = lastName,
            username = username,
            id = id,
            portfolioUrl = portfolioUrl
        ),
        userProfileImageEntity = UserProfileImageEntity(
            userId = id,
            small = profileImage.small,
            medium = profileImage.medium,
            large = profileImage.large
        )
    )
}

private fun UserWithProfileImage?.toUserItem(): UserItem? {
    return this?.let {
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

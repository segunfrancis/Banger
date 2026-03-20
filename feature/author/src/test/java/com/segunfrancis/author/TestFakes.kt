package com.segunfrancis.author

import com.segunfrancis.local.LinksEntity
import com.segunfrancis.local.PhotoForCaching
import com.segunfrancis.local.PhotosResponseEntity
import com.segunfrancis.local.UrlsEntity
import com.segunfrancis.local.UserEntity
import com.segunfrancis.local.UserProfileImageEntity
import com.segunfrancis.local.UserWithProfileImage
import com.segunfrancis.local.WDDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

open class EmptyWDDao : WDDao {
    override suspend fun insertPhoto(photosResponseEntity: PhotosResponseEntity) = Unit
    override suspend fun insertUser(userEntity: UserEntity) = Unit
    override suspend fun insertUrls(urlsEntity: UrlsEntity) = Unit
    override suspend fun insertUserProfileImage(userProfileImageEntity: UserProfileImageEntity) = Unit
    override suspend fun insertLinks(linksEntity: LinksEntity) = Unit
    override suspend fun updateFavouriteStatus(photoId: String, isFavourite: Boolean) = Unit
    override fun getAllFavouritePhotos(): Flow<List<PhotoForCaching>> = emptyFlow()
    override fun getPhotosByCategory(category: String): Flow<List<PhotoForCaching>> = emptyFlow()
    override fun getPhotoById(id: String): Flow<PhotoForCaching?> = emptyFlow()
    override fun getAuthorDetailsByUsername(username: String): Flow<UserWithProfileImage?> = emptyFlow()
    override suspend fun updateAuthorFavouriteStatus(username: String, isFavourite: Boolean) = Unit
    override fun getFavouriteAuthors(): Flow<List<UserWithProfileImage>> = emptyFlow()
}

object TestData {
    fun userWithProfileImage(id: String, isFavourite: Boolean): UserWithProfileImage {
        return UserWithProfileImage(
            userEntity = UserEntity(
                photoId = "photo-$id",
                bio = "bio",
                firstName = "First",
                id = id,
                lastName = "Last",
                name = "First Last",
                portfolioUrl = null,
                username = id,
                isFavourite = isFavourite
            ),
            userProfileImageEntity = UserProfileImageEntity(
                userId = id,
                large = "large",
                medium = "medium",
                small = "small"
            )
        )
    }
}

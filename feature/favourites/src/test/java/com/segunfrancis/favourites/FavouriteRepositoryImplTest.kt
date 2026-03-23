package com.segunfrancis.favourites

import com.google.common.truth.Truth.assertThat
import com.segunfrancis.favourites.ui.domain.FavouriteRepositoryImpl
import com.segunfrancis.local.LinksEntity
import com.segunfrancis.local.PhotoForCaching
import com.segunfrancis.local.PhotosResponseEntity
import com.segunfrancis.local.UrlsEntity
import com.segunfrancis.local.UserEntity
import com.segunfrancis.local.UserProfileImageEntity
import com.segunfrancis.local.UserWithProfileImage
import com.segunfrancis.local.WDDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteRepositoryImplTest {

    @Test
    fun `getFavourites maps dao entities`() = runTest {
        val dao = object : EmptyWDDao() {
            override fun getAllFavouritePhotos(): Flow<List<PhotoForCaching>> {
                return flowOf(listOf(samplePhotoForCaching("photo-1")))
            }
        }
        val repository = FavouriteRepositoryImpl(dao, StandardTestDispatcher(testScheduler))

        val result = repository.getFavourites().first()

        assertThat(result).hasSize(1)
        assertThat(result.first().id).isEqualTo("photo-1")
        assertThat(result.first().urls.thumb).isEqualTo("thumb")
    }

    private open class EmptyWDDao : WDDao {
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

    private fun samplePhotoForCaching(id: String): PhotoForCaching {
        return PhotoForCaching(
            photosResponseEntity = PhotosResponseEntity(
                id = id,
                description = "desc",
                altDescription = "alt",
                blurHash = "LKO2?U%2Tw=w]~RBVZRi};RPxuwH",
                height = 100,
                width = 100,
                likes = 5,
                isFavourite = true,
                category = "cat"
            ),
            userWithProfileImage = null,
            urlsEntity = UrlsEntity(
                photoId = id,
                full = "full",
                raw = "raw",
                regular = "regular",
                small = "small",
                thumb = "thumb"
            ),
            linksEntity = null
        )
    }
}

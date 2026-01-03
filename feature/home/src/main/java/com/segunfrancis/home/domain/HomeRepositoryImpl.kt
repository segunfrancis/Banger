package com.segunfrancis.home.domain

import com.segunfrancis.local.LinksEntity
import com.segunfrancis.local.PhotoForCaching
import com.segunfrancis.local.PhotosResponseEntity
import com.segunfrancis.local.UrlsEntity
import com.segunfrancis.local.UserEntity
import com.segunfrancis.local.UserProfileImageEntity
import com.segunfrancis.local.UserWithProfileImage
import com.segunfrancis.local.WDDao
import com.segunfrancis.remote.PhotoOrientation
import com.segunfrancis.remote.PhotosResponseItem
import com.segunfrancis.utility.BlurHashDecoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val api: HomeApi,
    private val dao: WDDao
) :
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
                val photos =
                    api.getRandomPhotos(orientation = orientation.name.lowercase(), query = query)
                query to photos.map { photo -> photo.toHomePhoto() }
            }
            Result.success(photos)
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
    }

    override suspend fun getPhotos(
        query: String,
        orientation: PhotoOrientation,
    ): Flow<List<HomePhoto>> {
        return channelFlow {
            launch(dispatcher) {
                val photos = api.getRandomPhotos(
                    orientation = orientation.name.lowercase(),
                    query = query
                )
                dao.insertPhoto(*photos.map { it.toPhotoForCaching(query) }.toTypedArray())
            }
            dao.getPhotosByCategory(query).collect { photos ->
                send(photos.map { photo -> photo.toHomePhoto() }.asReversed())
            }
        }.flowOn(dispatcher)
    }

    private fun PhotosResponseItem.toHomePhoto(): HomePhoto {
        return with(this) {
            HomePhoto(
                id = id,
                description = description,
                blurHash = blurHash,
                thumb = urls.thumb,
                blurHashBitmap = BlurHashDecoder.decode(
                    blurHash = blurHash,
                    width = width.div(100),
                    height = height.div(100)
                ),
                altDescription = altDescription,
                height = height,
                width = width,
                likes = likes
            )
        }
    }

    private fun PhotoForCaching.toHomePhoto(): HomePhoto {
        return with(this) {
            HomePhoto(
                id = photosResponseEntity.id,
                description = photosResponseEntity.description,
                altDescription = photosResponseEntity.altDescription,
                height = photosResponseEntity.height,
                width = photosResponseEntity.width,
                blurHash = photosResponseEntity.blurHash,
                thumb = urlsEntity?.thumb.orEmpty(),
                likes = photosResponseEntity.likes,
                blurHashBitmap = BlurHashDecoder.decode(
                    blurHash = photosResponseEntity.blurHash,
                    width = photosResponseEntity.width.div(100),
                    height = photosResponseEntity.height.div(100)
                )
            )
        }
    }

    private fun PhotosResponseItem.toPhotoForCaching(category: String): PhotoForCaching {
        return with(this) {
            val photosResponseEntity = PhotosResponseEntity(
                id = id,
                description = description,
                altDescription = altDescription,
                blurHash = blurHash,
                height = height,
                width = width,
                likes = likes,
                category = category
            )
            val userEntity = UserEntity(
                photoId = id,
                bio = user.bio,
                firstName = user.firstName,
                lastName = user.lastName,
                id = user.id,
                name = user.name,
                portfolioUrl = user.portfolioUrl,
                username = user.username
            )
            val urlsEntity = UrlsEntity(
                photoId = id,
                full = urls.full,
                regular = urls.regular,
                small = urls.small,
                thumb = urls.thumb,
                raw = urls.raw
            )
            val userProfileImageEntity = UserProfileImageEntity(
                userId = user.id,
                large = user.profileImage.large,
                medium = user.profileImage.medium,
                small = user.profileImage.small
            )
            PhotoForCaching(
                photosResponseEntity = photosResponseEntity,
                userWithProfileImage = UserWithProfileImage(
                    userEntity = userEntity,
                    userProfileImageEntity = userProfileImageEntity
                ),
                urlsEntity = urlsEntity,
                linksEntity = LinksEntity(
                    photoId = id,
                    download = links.download,
                    downloadLocation = links.downloadLocation
                )
            )
        }
    }
}

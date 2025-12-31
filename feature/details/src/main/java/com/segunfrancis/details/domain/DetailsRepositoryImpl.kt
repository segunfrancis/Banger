package com.segunfrancis.details.domain

import android.Manifest
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresPermission
import com.segunfrancis.details.domain.data.DetailsApi
import com.segunfrancis.local.PhotoForCaching
import com.segunfrancis.local.PhotoWithUser
import com.segunfrancis.local.PhotosResponseEntity
import com.segunfrancis.local.UrlsEntity
import com.segunfrancis.local.UserEntity
import com.segunfrancis.local.UserLinksEntity
import com.segunfrancis.local.UserProfileImageEntity
import com.segunfrancis.local.WDDao
import com.segunfrancis.remote.DownloadResponse
import com.segunfrancis.remote.PhotosResponseItem
import com.segunfrancis.utility.BlurHashDecoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

class DetailsRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val api: DetailsApi,
    private val context: Context,
    private val dao: WDDao
) : DetailsRepository {
    override suspend fun getPhotoDetails(id: String): Result<Flow<DetailPhoto>> {
        return try {
            Result.success(
                dao.getPhotoById(id)
                    .map { it.toDetailPhoto() }
                    .flowOn(dispatcher)
            )
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun trackDownload(id: String): Result<DownloadResponse> {
        return try {
            val response = withContext(dispatcher) { api.trackDownload(id) }
            Result.success(response)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun downloadImage(url: String): Result<Uri?> {
        return try {
            val downloadUrl = withContext(dispatcher) { api.initDownloadImage(url) }.url
            val response = withContext(dispatcher) { api.downloadImage(downloadUrl) }
            if (response.isSuccessful) {
                val imageMimeType = response.body()?.contentType()?.toString() ?: "image/*"
                val extension = response.body()?.contentType()?.subtype ?: "jpg"
                val fileName = "unsplash_${System.currentTimeMillis()}.$extension"
                val collection = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                else MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val contentResolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, imageMimeType)
                    put(MediaStore.Images.Media.IS_PENDING, 1) // Mark as pending
                }
                val imageUri = contentResolver.insert(collection, contentValues)?.also { uri ->
                    contentResolver.openOutputStream(uri)?.use { outputStream ->
                        response.body()?.byteStream()?.use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
                // Mark as completed
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                imageUri?.let { contentResolver.update(it, contentValues, null, null) }
                Result.success(imageUri)
            } else {
                Result.failure(IOException("Failed to download: ${response.code()}"))
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
    }

    @RequiresPermission(Manifest.permission.SET_WALLPAPER)
    override suspend fun setHomeLockScreenFromUri(
        imageUri: Uri,
        option: WallpaperOption
    ): Result<Unit> {
        return withContext(dispatcher) {
            try {
                val wallpaperManager = WallpaperManager.getInstance(context)
                context.contentResolver.openInputStream(imageUri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.let { bitmap ->
                        val flags = when (option) {
                            WallpaperOption.HomeScreen -> WallpaperManager.FLAG_SYSTEM
                            WallpaperOption.LockScreen -> WallpaperManager.FLAG_LOCK
                            WallpaperOption.HomeAndLockScreen -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                        }
                        wallpaperManager.setBitmap(bitmap, null, true, flags)
                        Result.success(Unit)
                    } ?: Result.failure(IllegalStateException("Failed to decode bitmap"))
                } ?: Result.failure(IllegalStateException("Failed to open input stream"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun addPhotoToFavourite(item: PhotosResponseItem) {
        withContext(dispatcher) {
            dao.insertPhotoWithUser(
                photosResponseEntity = item.toPhotoEntity(""), // TODO: supply correct value
                userEntity = item.toUsersEntity(),
                urlsEntity = item.toUrlsEntity(),
                userProfileImageEntity = item.toUserProfileImageEntity(),
                userLinksEntity = item.toUserLinksEntity()
            )
        }
    }

    override suspend fun removePhotoFromFavourite(photoId: String) {
        withContext(dispatcher) {
            dao.deletePhotoById(id = photoId)
        }
    }

    override fun getPhotoById(photoId: String): Result<Flow<PhotoWithUser?>> {
        return try {
            Result.success(dao.getPhotoWithUserById(id = photoId).flowOn(dispatcher))
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun updateFavouriteStatus(
        photoId: String,
        isFavourite: Boolean
    ): Result<Unit> {
        return try {
            dao.updateFavouriteStatus(photoId, isFavourite)
            Result.success(Unit)
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
    }

    private fun PhotosResponseItem.toPhotoEntity(category: String): PhotosResponseEntity {
        return with(this) {
            PhotosResponseEntity(
                id = id,
                description = description,
                altDescription = altDescription,
                blurHash = blurHash,
                width = width,
                height = height,
                likes = likes,
                category = category
            )
        }
    }

    private fun PhotosResponseItem.toUsersEntity(): UserEntity {
        val photoId = id
        return with(this.user) {
            UserEntity(
                photoId = photoId,
                id = id,
                bio = bio,
                name = name,
                lastName = lastName,
                username = username,
                firstName = firstName,
                portfolioUrl = portfolioUrl
            )
        }
    }

    private fun PhotosResponseItem.toUrlsEntity(): UrlsEntity {
        return with(this.urls) {
            UrlsEntity(
                photoId = id,
                full = full,
                raw = raw,
                thumb = thumb,
                small = small,
                regular = regular
            )
        }
    }

    private fun PhotosResponseItem.toUserProfileImageEntity(): UserProfileImageEntity {
        return with(this.user.profileImage) {
            UserProfileImageEntity(
                userId = user.id,
                small = small,
                large = large,
                medium = medium
            )
        }
    }

    private fun PhotosResponseItem.toUserLinksEntity(): UserLinksEntity {
        return with(this.user.links) {
            UserLinksEntity(
                userId = user.id,
                self = self,
                html = html,
                likes = likes,
                photos = photos,
                portfolio = portfolio
            )
        }
    }

    private fun PhotoForCaching.toDetailPhoto(): DetailPhoto {
        return with(this.photosResponseEntity) {
            DetailPhoto(
                id = id,
                description = description,
                blurHash = blurHash,
                thumb = urlsEntity?.thumb.orEmpty(),
                altDescription = altDescription,
                height = height,
                width = width,
                likes = likes,
                isFavourite = isFavourite,
                blurHashBitmap = BlurHashDecoder.decode(
                    blurHash = blurHash,
                    width = width.div(100),
                    height = height.div(100)
                ),
                username = userWithProfileImage?.userEntity?.username.orEmpty(),
                name = userWithProfileImage?.userEntity?.name.orEmpty(),
                photoUrl = urlsEntity?.regular.orEmpty(),
                bio = userWithProfileImage?.userEntity?.bio.orEmpty(),
                profileImage = userWithProfileImage?.userProfileImageEntity?.large.orEmpty(),
                downloadLocation = linksEntity?.downloadLocation.orEmpty()
            )
        }
    }
}

enum class WallpaperOption(val title: String) {
    HomeScreen("Home Screen"),
    LockScreen("Lock Screen"),
    HomeAndLockScreen("Home Screen and Lock Screen")
}

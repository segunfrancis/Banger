package com.segunfrancis.details.domain

import android.net.Uri
import com.segunfrancis.local.PhotoWithUser
import com.segunfrancis.remote.DownloadResponse
import com.segunfrancis.remote.PhotosResponseItem
import kotlinx.coroutines.flow.Flow

interface DetailsRepository {
    suspend fun getPhotoDetails(id: String): Result<PhotosResponseItem>
    suspend fun trackDownload(id: String): Result<DownloadResponse>
    suspend fun downloadImage(url: String): Result<Uri?>
    suspend fun addPhotoToFavourite(item: PhotosResponseItem)
    suspend fun removePhotoFromFavourite(photoId: String)
    fun getPhotoById(photoId: String): Result<Flow<PhotoWithUser?>>
    suspend fun setHomeLockScreenFromUri(imageUri: Uri, option: WallpaperOption) : Result<Unit>
}

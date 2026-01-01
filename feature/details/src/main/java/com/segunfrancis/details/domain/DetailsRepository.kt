package com.segunfrancis.details.domain

import android.net.Uri
import com.segunfrancis.local.PhotoWithUser
import com.segunfrancis.remote.DownloadResponse
import kotlinx.coroutines.flow.Flow

interface DetailsRepository {
    suspend fun getPhotoDetails(id: String): Result<Flow<DetailPhoto>>
    suspend fun trackDownload(id: String): Result<DownloadResponse>
    suspend fun downloadImage(url: String): Result<Uri?>
    fun getPhotoById(photoId: String): Result<Flow<PhotoWithUser?>>
    suspend fun setHomeLockScreenFromUri(imageUri: Uri, option: WallpaperOption) : Result<Unit>
    suspend fun updateFavouriteStatus(photoId: String, isFavourite: Boolean): Result<Unit>
}

package com.segunfrancis.details.domain

import android.net.Uri
import com.segunfrancis.remote.DownloadResponse
import com.segunfrancis.remote.PhotosResponseItem

interface DetailsRepository {
    suspend fun getPhotoDetails(id: String): Result<PhotosResponseItem>
    suspend fun trackDownload(id: String): Result<DownloadResponse>
    suspend fun downloadImage(url: String): Result<Uri?>
}

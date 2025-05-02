package com.segunfrancis.details.domain

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.segunfrancis.details.domain.data.DetailsApi
import com.segunfrancis.remote.DownloadResponse
import com.segunfrancis.remote.PhotosResponseItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException

class DetailsRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val api: DetailsApi,
    private val context: Context
) : DetailsRepository {
    override suspend fun getPhotoDetails(id: String): Result<PhotosResponseItem> {
        return try {
            val response = withContext(dispatcher) { api.getPhotoDetails(id) }
            Result.success(response)
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
}

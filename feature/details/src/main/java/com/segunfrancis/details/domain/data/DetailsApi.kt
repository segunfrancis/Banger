package com.segunfrancis.details.domain.data

import com.segunfrancis.remote.DownloadResponse
import com.segunfrancis.remote.PhotosResponseItem
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DetailsApi {

    @GET("photos/{id}")
    suspend fun getPhotoDetails(@Path("id") id: String) : PhotosResponseItem

    @GET("/photos/{id}/download")
    suspend fun trackDownload(@Path("id") id: String) : DownloadResponse

    @GET
    suspend fun initDownloadImage(@Url url: String): DownloadResponse

    @Streaming
    @GET
    suspend fun downloadImage(@Url imageUrl: String): Response<ResponseBody>
}

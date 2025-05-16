package com.segunfrancis.home.domain

import com.segunfrancis.remote.PHOTOS_NORMAL_SIZE
import com.segunfrancis.remote.PhotosResponseItem
import com.segunfrancis.remote.RANDOM_PHOTO_SIZE
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {

    @GET("photos")
    suspend fun getPhotos(@Query("per_page") perPage: Int = PHOTOS_NORMAL_SIZE): List<PhotosResponseItem>

    @GET("photos/random")
    suspend fun getRandomPhotos(
        @Query("orientation") orientation: String,
        @Query("query") query: String? = null,
        @Query("count") count: Int = RANDOM_PHOTO_SIZE
    ): List<PhotosResponseItem>
}

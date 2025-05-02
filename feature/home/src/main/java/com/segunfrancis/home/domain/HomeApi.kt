package com.segunfrancis.home.domain

import com.segunfrancis.remote.PhotosResponseItem
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {

    @GET("photos")
    suspend fun getPhotos(@Query("per_page") perPage: Int = 50) : List<PhotosResponseItem>
}

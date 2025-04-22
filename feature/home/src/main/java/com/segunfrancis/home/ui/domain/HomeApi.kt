package com.segunfrancis.home.ui.domain

import retrofit2.http.GET

interface HomeApi {

    @GET("photos")
    suspend fun getPhotos() : List<PhotosResponseItem>
}

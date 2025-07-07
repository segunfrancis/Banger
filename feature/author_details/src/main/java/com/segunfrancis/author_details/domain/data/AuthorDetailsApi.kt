package com.segunfrancis.author_details.domain.data

import com.segunfrancis.remote.UserPhotosResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface AuthorDetailsApi {

    @GET("users/{username}/photos")
    suspend fun getUserPhotos(@Path("username") username: String): List<UserPhotosResponse>
}

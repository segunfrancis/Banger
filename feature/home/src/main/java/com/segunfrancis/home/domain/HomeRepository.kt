package com.segunfrancis.home.domain

import com.segunfrancis.remote.PhotosResponseItem

interface HomeRepository {

    suspend fun getPhotos(): Result<List<PhotosResponseItem>>
}

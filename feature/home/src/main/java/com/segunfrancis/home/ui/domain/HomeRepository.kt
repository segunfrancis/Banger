package com.segunfrancis.home.ui.domain

interface HomeRepository {

    suspend fun getPhotos(): Result<List<PhotosResponseItem>>
}

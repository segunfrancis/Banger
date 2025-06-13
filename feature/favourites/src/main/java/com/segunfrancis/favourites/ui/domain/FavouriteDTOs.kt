package com.segunfrancis.favourites.ui.domain

import coil3.Bitmap

data class FavouritePhotoItem(
    val id: String,
    val description: String?,
    val altDescription: String?,
    val blurHashBitmap: Bitmap?,
    val height: Int,
    val width: Int,
    val likes: Int,
    val urls: FavouritePhotoUrls
)

data class FavouritePhotoUrls(
    val photoId: String,
    val full: String,
    val raw: String,
    val regular: String,
    val small: String,
    val thumb: String
)

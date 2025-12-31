package com.segunfrancis.details.domain

import android.graphics.Bitmap

data class DetailPhoto(
    val id: String,
    val description: String?,
    val blurHash: String?,
    val thumb: String,
    val blurHashBitmap: Bitmap?,
    val altDescription: String?,
    val height: Int,
    val width: Int,
    val likes: Int,
    val isFavourite: Boolean,
    val profileImage: String,
    val username: String,
    val name: String,
    val photoUrl: String,
    val bio: String,
    val downloadLocation: String
)

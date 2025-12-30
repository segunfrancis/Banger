package com.segunfrancis.home.domain

import android.graphics.Bitmap

data class HomePhoto(
    val id: String,
    val description: String?,
    val blurHash: String?,
    val thumb: String,
    val blurHashBitmap: Bitmap?,
    val altDescription: String?,
    val height: Int,
    val width: Int,
    val likes: Int
)

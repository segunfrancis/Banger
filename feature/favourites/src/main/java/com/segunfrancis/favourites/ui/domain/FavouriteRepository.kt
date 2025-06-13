package com.segunfrancis.favourites.ui.domain

import kotlinx.coroutines.flow.Flow

interface FavouriteRepository {
    fun getFavourites(): Flow<List<FavouritePhotoItem>>
}

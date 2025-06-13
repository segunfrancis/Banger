package com.segunfrancis.favourites.ui.domain

import com.segunfrancis.local.PhotoWithUrls
import com.segunfrancis.local.WDDao
import com.segunfrancis.utility.BlurHashDecoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class FavouriteRepositoryImpl(private val dao: WDDao, private val dispatcher: CoroutineDispatcher) :
    FavouriteRepository {
    override fun getFavourites(): Flow<List<FavouritePhotoItem>> {
        return dao.getAllPhotoWithUrls().flowOn(dispatcher).map { photos ->
            photos.map { photo -> photo.toFavouritePhotoItems() }
        }
    }

    private fun PhotoWithUrls.toFavouritePhotoItems(): FavouritePhotoItem {
        return with(this) {
            FavouritePhotoItem(
                id = photosResponseEntity.id,
                description = photosResponseEntity.description,
                altDescription = photosResponseEntity.altDescription,
                blurHashBitmap = BlurHashDecoder.decode(
                    blurHash = photosResponseEntity.blurHash,
                    width = photosResponseEntity.width.div(100),
                    height = photosResponseEntity.height.div(100)
                ),
                likes = photosResponseEntity.likes,
                height = photosResponseEntity.height,
                width = photosResponseEntity.width,
                urls = FavouritePhotoUrls(
                    photoId = urlsEntity.photoId,
                    full = urlsEntity.full,
                    raw = urlsEntity.raw,
                    small = urlsEntity.small,
                    thumb = urlsEntity.thumb,
                    regular = urlsEntity.regular
                )
            )
        }
    }
}

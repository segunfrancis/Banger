package com.segunfrancis.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WDDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photosResponseEntity: PhotosResponseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userEntity: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUrls(urlsEntity: UrlsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfileImage(userProfileImageEntity: UserProfileImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserLinks(userLinksEntity: UserLinksEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinks(linksEntity: LinksEntity)

    @Transaction
    suspend fun insertPhoto(vararg photos: PhotoForCaching) {
        for (photo in photos) {
            insertPhoto(photo.photosResponseEntity)
            photo.userWithProfileImage?.userEntity?.let { insertUser(it) }
            photo.urlsEntity?.let { insertUrls(it) }
            photo.userWithProfileImage?.userProfileImageEntity?.let { insertUserProfileImage(it) }
            photo.linksEntity?.let { insertLinks(it) }
        }
    }

    @Query("UPDATE photo_response SET isFavourite = :isFavourite WHERE id = :photoId")
    suspend fun updateFavouriteStatus(photoId: String, isFavourite: Boolean)

    @Transaction
    @Query("SELECT * FROM photo_response WHERE isFavourite = 1")
    fun getAllFavouritePhotos(): Flow<List<PhotoForCaching>>

    @Query("SELECT * FROM photo_response WHERE category IS :category")
    fun getPhotosByCategory(category: String): Flow<List<PhotoForCaching>>

    @Transaction
    @Query("SELECT * FROM photo_response WHERE id IS :id")
    fun getPhotoById(id: String): Flow<PhotoForCaching>

    @Transaction
    @Query("SELECT * FROM user WHERE username IS :username")
    fun getAuthorDetailsByUsername(username: String): Flow<UserWithProfileImage>

    @Query("UPDATE user SET isFavourite = :isFavourite WHERE username = :username")
    suspend fun updateAuthorFavouriteStatus(username: String, isFavourite: Boolean)

    @Query("SELECT * FROM user WHERE isFavourite == 1")
    fun getFavouriteAuthors(): Flow<List<UserWithProfileImage>>
}

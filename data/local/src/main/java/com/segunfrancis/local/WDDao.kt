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

    @Transaction
    suspend fun insertPhotoWithUser(
        photosResponseEntity: PhotosResponseEntity,
        userEntity: UserEntity,
        urlsEntity: UrlsEntity,
        userProfileImageEntity: UserProfileImageEntity,
        userLinksEntity: UserLinksEntity
    ) {
        insertPhoto(photosResponseEntity)
        insertUser(userEntity)
        insertUrls(urlsEntity)
        insertUserProfileImage(userProfileImageEntity)
        insertUserLinks(userLinksEntity)
    }

    @Transaction
    @Query("SELECT * FROM photo_response")
    fun getAllPhotoWithUrls(): Flow<List<PhotoWithUrls>>

    @Transaction
    @Query("SELECT * FROM photo_response WHERE id IS :id")
    fun getPhotoWithUserById(id: String): Flow<PhotoWithUser?>

    @Query("DELETE FROM photo_response WHERE id IS :id")
    fun deletePhotoById(id: String)
}

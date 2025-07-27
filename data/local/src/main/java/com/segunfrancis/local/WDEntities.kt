package com.segunfrancis.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "photo_response")
data class PhotosResponseEntity(
    @PrimaryKey val id: String,
    val description: String?,
    val altDescription: String?,
    val blurHash: String?,
    val height: Int,
    val width: Int,
    val likes: Int
)

@Entity(
    tableName = "user",
    foreignKeys = [
        ForeignKey(
            entity = PhotosResponseEntity::class,
            parentColumns = ["id"],
            childColumns = ["photoId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserEntity(
    val photoId: String,
    val bio: String?,
    val firstName: String,
    @PrimaryKey val id: String,
    val lastName: String?,
    val name: String,
    val portfolioUrl: String?,
    val username: String
)

@Entity(
    tableName = "urls",
    foreignKeys = [
        ForeignKey(
            entity = PhotosResponseEntity::class,
            parentColumns = ["id"],
            childColumns = ["photoId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UrlsEntity(
    @PrimaryKey val photoId: String,
    val full: String,
    val raw: String,
    val regular: String,
    val small: String,
    val thumb: String
)

@Entity(
    tableName = "user_profile_image",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserProfileImageEntity(
    @PrimaryKey val userId: String,
    val large: String,
    val medium: String,
    val small: String
)

@Entity(
    tableName = "user_links",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserLinksEntity(
    @PrimaryKey val userId: String,
    val html: String,
    val likes: String,
    val photos: String,
    val portfolio: String,
    val self: String
)

data class PhotoWithUser(
    @Embedded
    val photosResponseEntity: PhotosResponseEntity,

    @Relation(parentColumn = "id", entityColumn = "photoId")
    val userEntity: UserEntity,

    @Relation(parentColumn = "id", entityColumn = "photoId")
    val urlsEntity: UrlsEntity,

    @Relation(parentColumn = "id", entityColumn = "userId")
    val userProfileImageEntity: UserProfileImageEntity?,

    @Relation(parentColumn = "id", entityColumn = "userId")
    val userLinksEntity: UserLinksEntity?
)

data class PhotoWithUrls(
    @Embedded
    val photosResponseEntity: PhotosResponseEntity,

    @Relation(parentColumn = "id", entityColumn = "photoId")
    val urlsEntity: UrlsEntity,
)

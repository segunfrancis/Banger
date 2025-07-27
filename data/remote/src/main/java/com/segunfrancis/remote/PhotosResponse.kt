package com.segunfrancis.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotosResponseItem(
    @SerialName("alt_description")
    val altDescription: String?,
    @SerialName("asset_type")
    val assetType: String?,
    @SerialName("blur_hash")
    val blurHash: String?,
    val color: String?,
    @SerialName("created_at")
    val createdAt: String,
    val description: String?,
    val height: Int,
    val id: String,
    @SerialName("liked_by_user")
    val likedByUser: Boolean,
    val likes: Int,
    val links: Links,
    val slug: String?,
    @SerialName("updated_at")
    val updatedAt: String,
    val urls: Urls,
    val user: User,
    val width: Int
)

@Serializable
data class Links(
    val download: String,
    @SerialName("download_location")
    val downloadLocation: String?,
    val html: String,
    val self: String
)

@Serializable
data class UserLinks(
    val html: String,
    val likes: String,
    val photos: String,
    val portfolio: String,
    val self: String
)

@Serializable
data class ProfileImage(
    val large: String,
    val medium: String,
    val small: String
)

@Serializable
data class Social(
    @SerialName("portfolio_url")
    val portfolioUrl: String?
)

@Serializable
data class Urls(
    val full: String,
    val raw: String,
    val regular: String,
    val small: String,
    val smallS3: String? = null,
    val thumb: String
)

@Serializable
data class User(
    val bio: String?,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("for_hire")
    val forHire: Boolean,
    val id: String,
    @SerialName("last_name")
    val lastName: String?,
    val links: UserLinks,
    val name: String,
    @SerialName("portfolio_url")
    val portfolioUrl: String?,
    @SerialName("profile_image")
    val profileImage: ProfileImage,
    val social: Social,
    val username: String
)

@Serializable
data class UserPhotosResponse(
    val id: String,
    val width: Int,
    val height: Int,
    @SerialName("blur_hash") val blurHash: String,
    val urls: Urls
)

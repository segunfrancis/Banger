package com.segunfrancis.home.ui.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotosResponseItem(
    @SerialName("alt_description")
    val altDescription: String,
    @SerialName("asset_type")
    val assetType: String,
    @SerialName("blur_hash")
    val blurHash: String,
    @SerialName("color")
    val color: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("description")
    val description: String?,
    @SerialName("height")
    val height: Int,
    @SerialName("id")
    val id: String,
    @SerialName("liked_by_user")
    val likedByUser: Boolean,
    @SerialName("likes")
    val likes: Int,
    @SerialName("links")
    val links: Links,
    @SerialName("slug")
    val slug: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("urls")
    val urls: Urls,
    @SerialName("user")
    val user: User,
    @SerialName("width")
    val width: Int
)

@Serializable
data class Links(
    @SerialName("download")
    val download: String,
    @SerialName("download_location")
    val downloadLocation: String?,
    @SerialName("html")
    val html: String,
    @SerialName("self")
    val self: String
)

@Serializable
data class UserLinks(
    @SerialName("html")
    val html: String,
    @SerialName("likes")
    val likes: String,
    @SerialName("photos")
    val photos: String,
    @SerialName("portfolio")
    val portfolio: String,
    @SerialName("self")
    val self: String
)

@Serializable
data class ProfileImage(
    @SerialName("large")
    val large: String,
    @SerialName("medium")
    val medium: String,
    @SerialName("small")
    val small: String
)

@Serializable
data class Social(
    @SerialName("portfolio_url")
    val portfolioUrl: String?
)

@Serializable
data class Urls(
    @SerialName("full")
    val full: String,
    @SerialName("raw")
    val raw: String,
    @SerialName("regular")
    val regular: String,
    @SerialName("small")
    val small: String,
    @SerialName("small_s3")
    val smallS3: String,
    @SerialName("thumb")
    val thumb: String
)

@Serializable
data class User(
    @SerialName("bio")
    val bio: String?,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("for_hire")
    val forHire: Boolean,
    @SerialName("id")
    val id: String,
    @SerialName("last_name")
    val lastName: String?,
    @SerialName("links")
    val links: UserLinks,
    @SerialName("name")
    val name: String,
    @SerialName("portfolio_url")
    val portfolioUrl: String?,
    @SerialName("profile_image")
    val profileImage: ProfileImage,
    @SerialName("social")
    val social: Social,
    @SerialName("username")
    val username: String
)

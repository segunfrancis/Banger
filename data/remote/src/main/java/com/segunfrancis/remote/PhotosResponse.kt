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
    val blurHash: String,
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

val samplePhotoItem = PhotosResponseItem(
    altDescription = "A beautiful mountain view",
    assetType = "photo",
    blurHash = "LKO2?U%2Tw=w]~RBVZRi};RPxuwH",
    color = "#AABBCC",
    createdAt = "2023-01-01T00:00:00Z",
    description = "An awe-inspiring sunset behind a mountain range.",
    height = 1080,
    id = "sample-id-123",
    likedByUser = false,
    likes = 152,
    links = Links(
        download = "https://example.com/download.jpg",
        downloadLocation = "https://example.com/download-location",
        html = "https://example.com/photo-page",
        self = "https://example.com/api/photo"
    ),
    slug = "beautiful-mountain-view",
    updatedAt = "2023-01-05T12:00:00Z",
    urls = Urls(
        full = "https://example.com/full.jpg",
        raw = "https://example.com/raw.jpg",
        regular = "https://example.com/regular.jpg",
        small = "https://example.com/small.jpg",
        smallS3 = "https://example.com/small-s3.jpg",
        thumb = "https://example.com/thumb.jpg"
    ),
    user = User(
        bio = "Nature photographer and world explorer.",
        firstName = "Grace",
        forHire = true,
        id = "user-id-456",
        lastName = "Onaghise",
        links = UserLinks(
            html = "https://example.com/user",
            likes = "https://example.com/user/likes",
            photos = "https://example.com/user/photos",
            portfolio = "https://example.com/user/portfolio",
            self = "https://example.com/api/user"
        ),
        name = "Grace Onaghise",
        portfolioUrl = "https://portfolio.grace.com",
        profileImage = ProfileImage(
            large = "https://example.com/profile_large.jpg",
            medium = "https://example.com/profile_medium.jpg",
            small = "https://example.com/profile_small.jpg"
        ),
        social = Social(
            portfolioUrl = "https://portfolio.grace.com"
        ),
        username = "graceo"
    ),
    width = 1920
)

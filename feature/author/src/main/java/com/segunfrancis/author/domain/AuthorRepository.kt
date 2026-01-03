package com.segunfrancis.author.domain

import com.segunfrancis.local.UserWithProfileImage
import com.segunfrancis.local.WDDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

internal interface AuthorRepository {
    suspend fun getFavouriteAuthors(): Result<Flow<List<AuthorItem>>>
}

internal class AuthorRepositoryImpl(
    private val dao: WDDao,
    private val dispatcher: CoroutineDispatcher
) : AuthorRepository {
    override suspend fun getFavouriteAuthors(): Result<Flow<List<AuthorItem>>> {
        return try {
            Result.success(
                dao.getFavouriteAuthors()
                    .map { authors -> authors.map { it.toAuthorItem() } }
                    .flowOn(dispatcher)
            )
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}

data class AuthorItem(
    val bio: String?,
    val firstName: String,
    val id: String,
    val lastName: String?,
    val name: String,
    val portfolioUrl: String?,
    val username: String,
    val isFavourite: Boolean,
    val profilePhoto: String?
)

private fun UserWithProfileImage.toAuthorItem(): AuthorItem {
    return with(this) {
        AuthorItem(
            id = userEntity.id,
            bio = userEntity.bio,
            name = userEntity.name,
            firstName = userEntity.firstName,
            lastName = userEntity.lastName,
            username = userEntity.username,
            portfolioUrl = userEntity.portfolioUrl,
            isFavourite = userEntity.isFavourite,
            profilePhoto = userProfileImageEntity?.large
        )
    }
}

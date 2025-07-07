package com.segunfrancis.author_details.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.segunfrancis.author_details.domain.UserPhotos
import com.segunfrancis.theme.R
import com.segunfrancis.theme.WallpaperDownloaderTheme
import com.segunfrancis.theme.components.AppToolbar
import com.segunfrancis.utility.BlurHashDecoder
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthorDetailsScreen(
    onBackClick: () -> Unit,
    onImageClick: (imageId: String) -> Unit,
    name: String,
    username: String,
    bio: String,
    profileImage: String,
    blurHash: String
) {
    val viewModel = koinViewModel<AuthorDetailsViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AuthorDetailsContent(
        profileImage = profileImage,
        blurHash = blurHash,
        name = name,
        username = username,
        bio = bio,
        photos = uiState.photos,
        onAction = {
            when (it) {
                AuthorDetailsScreeAction.OnBackClick -> {
                    onBackClick()
                }

                AuthorDetailsScreeAction.OnSave -> {

                }

                is AuthorDetailsScreeAction.OnImageClick -> {
                    onImageClick(it.imageId)
                }
            }
        }
    )
}

@Composable
fun AuthorDetailsContent(
    profileImage: String,
    blurHash: String,
    name: String,
    username: String,
    bio: String,
    photos: List<UserPhotos>,
    onAction: (AuthorDetailsScreeAction) -> Unit
) {
    val blurBitmap =
        BlurHashDecoder.decode(blurHash = blurHash, width = 300, height = 300)
    Column(modifier = Modifier.fillMaxSize()) {
        AppToolbar(
            title = "About",
            navIcon = R.drawable.ic_arrow_back,
            actionIcon = R.drawable.ic_favorite,
            onNavIconClick = { onAction(AuthorDetailsScreeAction.OnBackClick) },
            onActionClick = { onAction(AuthorDetailsScreeAction.OnSave) }
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AsyncImage(
                        model = profileImage,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(170.dp)
                            .clip(CircleShape)
                            .border(width = 1.dp, color = Color.LightGray),
                        placeholder = rememberAsyncImagePainter(blurBitmap)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = name, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(4.dp))
                    Text(text = username, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = bio, modifier = Modifier
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "More by $name",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            items(photos) {
                Card(
                    onClick = { onAction(AuthorDetailsScreeAction.OnImageClick(it.id)) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.heightIn(min = 160.dp, max = 170.dp)
                ) {
                    AsyncImage(
                        model = it.photo,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = rememberAsyncImagePainter(it.blurHashBitmap),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

sealed interface AuthorDetailsScreeAction {
    data object OnBackClick : AuthorDetailsScreeAction
    data object OnSave : AuthorDetailsScreeAction
    data class OnImageClick(val imageId: String) : AuthorDetailsScreeAction
}

@PreviewLightDark
@Composable
fun AuthorDetailsPreview() {
    WallpaperDownloaderTheme {
        AuthorDetailsContent(
            profileImage = "",
            blurHash = "L7CF0C^k2JNf=GRPj=nO10RQ=vwb",
            name = "John Doe",
            username = "john.doe",
            bio = "Ethan Carter is a talented photographer known for his breathtaking landscape photography. His work captures the beauty and serenity of nature, inviting viewers to explore the world through his lens.",
            photos = listOf(),
            onAction = {}
        )
    }
}

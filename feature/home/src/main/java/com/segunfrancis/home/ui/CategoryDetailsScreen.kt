package com.segunfrancis.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.segunfrancis.home.domain.HomePhoto
import com.segunfrancis.theme.R
import com.segunfrancis.theme.components.AppToolbar
import com.segunfrancis.theme.components.DialogLoader
import com.segunfrancis.utility.toTitleCase
import org.koin.androidx.compose.koinViewModel

@Composable
fun CategoryDetailsScreen(title: String, navigateBack: () -> Unit, onPhotoClick: (String) -> Unit) {
    val viewModel = koinViewModel<CategoriesDetailsViewModel>()
    when (val uiState = viewModel.uiState.collectAsStateWithLifecycle().value) {
        is CategoriesDetailsUiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.message.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(24.dp),
                    textAlign = TextAlign.Center
                )
                Button(onClick = { viewModel.getPhotos() }) {
                    Text(text = "Retry")
                }
            }
        }

        CategoriesDetailsUiState.Loading -> {
            DialogLoader()
        }

        is CategoriesDetailsUiState.Success -> {
            CategoryDetailsContent(
                title = title.toTitleCase(),
                photos = uiState.homePhotos,
                navigateBack = navigateBack,
                onPhotoClick = onPhotoClick
            )
        }
    }
}

@PreviewLightDark
@Composable
fun CategoryDetailsContent(
    title: String = "Abstract",
    photos: List<HomePhoto> = listOf(homePhoto, homePhoto),
    navigateBack: () -> Unit = {},
    onPhotoClick: (String) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppToolbar(title = title, navIcon = R.drawable.ic_arrow_back, onNavIconClick = navigateBack)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(photos) { photo ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    onClick = { onPhotoClick(photo.id) }) {
                    AsyncImage(
                        model = photo.thumb,
                        contentDescription = photo.description,
                        placeholder = rememberAsyncImagePainter(photo.blurHashBitmap),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.height(300.dp),
                        error = painterResource(R.drawable.il_no_image)
                    )
                }
            }
        }
    }
}

val homePhoto = HomePhoto(
    id = "",
    description = "",
    blurHash = "",
    thumb = "",
    blurHashBitmap = null,
    altDescription = "",
    assetType = "",
    color = "",
    createdAt = "",
    height = 1,
    width = 2,
    likedByUser = true,
    likes = 123,
    slug = "",
    updatedAt = ""
)

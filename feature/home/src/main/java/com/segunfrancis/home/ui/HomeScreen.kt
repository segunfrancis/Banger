package com.segunfrancis.home.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.segunfrancis.theme.WallpaperDownloaderTheme
import com.segunfrancis.utility.toTitleCase
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(onPhotoClick: (String) -> Unit) {
    val viewModel = koinViewModel<HomeViewModel>()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.action.collect {
            when (it) {
                is HomeActions.ShowError -> {
                    Toast.makeText(context, it.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    HomeContent(
        isLoading = uiState.isLoading,
        onPhotoClick = onPhotoClick,
        homePhotos = uiState.homePhotos
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    isLoading: Boolean,
    homePhotos: List<Pair<String, List<PhotoItem>>>,
    onPhotoClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Home Screen",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(24.dp)
        )
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        LazyColumn(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            homePhotos.forEach { homePhoto ->
                if (homePhoto.second.isNotEmpty()) {
                    item(key = homePhoto.first) {
                        Column {
                            Text(
                                text = homePhoto.first.toTitleCase(),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                            HorizontalMultiBrowseCarousel(
                                modifier = Modifier
                                    .width(412.dp)
                                    .height(226.dp),
                                state = rememberCarouselState(itemCount = { homePhoto.second.size }),
                                preferredItemWidth = 200.dp,
                                itemSpacing = 8.dp,
                                contentPadding = PaddingValues(8.dp)
                            ) { index ->
                                val photo = homePhoto.second[index]
                                PhotoCard(
                                    modifier = Modifier
                                        .height(210.dp)
                                        .maskClip(RoundedCornerShape(8.dp)),
                                    photo = photo
                                ) { onPhotoClick(it) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoCard(modifier: Modifier = Modifier, photo: PhotoItem, onPhotoClick: (String) -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        onClick = { onPhotoClick(photo.id) }) {
        AsyncImage(
            model = photo.thumb,
            contentDescription = photo.description,
            placeholder = rememberAsyncImagePainter(photo.blurHashBitmap),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    WallpaperDownloaderTheme {
        HomeContent(
            isLoading = true,
            homePhotos = emptyList(),
            onPhotoClick = {}
        )
    }
}

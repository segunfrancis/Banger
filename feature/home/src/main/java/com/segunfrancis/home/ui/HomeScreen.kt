package com.segunfrancis.home.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.segunfrancis.remote.PhotosResponseItem
import com.segunfrancis.theme.WallpaperDownloaderTheme
import com.segunfrancis.utility.BlurHashDecoder
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(onPhotoClick: (String) -> Unit) {
    val viewModel = koinViewModel<HomeViewModel>()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    HomeContent(photos = uiState.photos, onPhotoClick = onPhotoClick)
    LaunchedEffect(Unit) {
        viewModel.action.collect {
            when (it) {
                is HomeActions.ShowError -> {
                    Toast.makeText(context, it.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

@Composable
fun HomeContent(photos: List<PhotosResponseItem>, onPhotoClick:(String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Home Screen",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(24.dp)
        )

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(photos) { photo ->
                val bitmap = BlurHashDecoder.decode(
                    photo.blurHash,
                    width = photo.width.div(10),
                    height = photo.height.div(10)
                )
                Card(modifier = Modifier, shape = RoundedCornerShape(4.dp), onClick = { onPhotoClick(photo.id) }) {
                    AsyncImage(
                        model = photo.urls.thumb,
                        contentDescription = photo.description,
                        placeholder = rememberAsyncImagePainter(bitmap),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    WallpaperDownloaderTheme {
        HomeContent(photos = emptyList(), onPhotoClick = {})
    }
}

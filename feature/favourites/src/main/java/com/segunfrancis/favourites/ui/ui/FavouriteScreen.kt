package com.segunfrancis.favourites.ui.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.segunfrancis.favourites.ui.domain.FavouritePhotoItem
import com.segunfrancis.favourites.ui.ui.FavouriteViewModel.FavouriteAction
import com.segunfrancis.theme.WallpaperDownloaderTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavouriteScreen() {
    val viewModel = koinViewModel<FavouriteViewModel>()
    val favouritePhotos by viewModel.favouritePhotos.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.action.collect {
            when (it) {
                is FavouriteAction.ShowError -> {
                    Toast.makeText(context, it.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    FavouriteContent(favouritePhotos)
}

@Composable
fun FavouriteContent(favouritePhotos: List<FavouritePhotoItem>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Favourite Screen",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(24.dp)
        )
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(items = favouritePhotos.asReversed()) {
                PhotoCard(photo = it) { }
            }
        }
    }
}

@Preview
@Composable
fun FavouriteScreenPreview() {
    WallpaperDownloaderTheme {
        FavouriteContent(favouritePhotos = emptyList())
    }
}

@Composable
fun PhotoCard(
    modifier: Modifier = Modifier,
    photo: FavouritePhotoItem,
    onPhotoClick: (String) -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        onClick = { onPhotoClick(photo.id) }) {
        AsyncImage(
            model = photo.urls.thumb,
            contentDescription = photo.description,
            placeholder = rememberAsyncImagePainter(photo.blurHashBitmap),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

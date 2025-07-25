package com.segunfrancis.favourites.ui.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.segunfrancis.favourites.ui.domain.FavouritePhotoItem
import com.segunfrancis.favourites.ui.ui.FavouriteViewModel.FavouriteAction
import com.segunfrancis.theme.R
import com.segunfrancis.theme.WallpaperDownloaderTheme
import com.segunfrancis.theme.components.AppToolbar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavouriteScreen(onMenuActionClick: () -> Unit) {
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

    FavouriteContent(favouritePhotos = favouritePhotos, onMenuActionClick = onMenuActionClick)
}

@Composable
fun FavouriteContent(favouritePhotos: List<FavouritePhotoItem>, onMenuActionClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppToolbar(
            title = "Favourite Screen",
            actionIcon = R.drawable.ic_settings,
            onActionClick = {
                onMenuActionClick()
            }
        )
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(items = favouritePhotos.asReversed()) {
                PhotoCard(photo = it) { }
            }
            if (favouritePhotos.isEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(Modifier.height(24.dp))
                        Image(
                            painter = painterResource(R.drawable.il_no_favourites),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "No saved wallpapers",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun FavouriteScreenPreview() {
    WallpaperDownloaderTheme {
        FavouriteContent(favouritePhotos = emptyList(), onMenuActionClick = {})
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

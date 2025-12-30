package com.segunfrancis.home.ui

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    CategoryDetailsContent(
        title = title.toTitleCase(),
        photos = uiState.homePhotos,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        navigateBack = navigateBack,
        onPhotoClick = onPhotoClick,
        onRetryClick = { viewModel.getPhotos() }
    )
    LaunchedEffect(Unit) {
        viewModel.action.collect {
            when (it) {
                is CategoriesDetailsActions.ShowError -> {
                    Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun CategoryDetailsContent(
    title: String = "Abstract",
    photos: List<HomePhoto> = listOf(homePhoto, homePhoto),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    navigateBack: () -> Unit = {},
    onPhotoClick: (String) -> Unit = {},
    onRetryClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppToolbar(title = title, navIcon = R.drawable.ic_arrow_back, onNavIconClick = navigateBack)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(items = photos, key = { it.id }) { photo ->
                Card(
                    modifier = Modifier.animateItem(),
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
        if (isLoading) {
            DialogLoader()
        }
    }
    errorMessage?.let {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(24.dp),
                textAlign = TextAlign.Center
            )
            Button(onClick = { onRetryClick() }) {
                Text(text = "Retry")
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
    height = 1,
    width = 2,
    likes = 123
)

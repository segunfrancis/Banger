package com.segunfrancis.details.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.segunfrancis.details.domain.DetailPhoto
import com.segunfrancis.details.domain.WallpaperOption
import com.segunfrancis.theme.R
import com.segunfrancis.theme.WallpaperDownloaderTheme
import com.segunfrancis.theme.components.AppPrimaryButton
import com.segunfrancis.theme.components.AppSecondaryButton
import com.segunfrancis.theme.components.AppToolbar
import com.segunfrancis.theme.components.DialogLoader
import com.segunfrancis.utility.BlurHashDecoder
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat

@Composable
fun DetailsScreen(
    onBackClick: () -> Unit,
    viewAuthorDetails: (id: String, name: String, username: String, bio: String, profileImage: String, blurHash: String) -> Unit
) {
    val viewModel = koinViewModel<DetailsViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var shouldShowWallpaperOptionDialog by rememberSaveable { mutableStateOf(false) }
    var isHomeChecked by rememberSaveable { mutableStateOf(true) }
    var isLockChecked by rememberSaveable { mutableStateOf(true) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.downloadImage()
        }
    }
    uiState.detailsError?.let {
        DetailsError(error = it) { viewModel.getPhotoDetails() }
    }
    if (uiState.isLoading) {
        DialogLoader()
    }
    if (shouldShowWallpaperOptionDialog) {
        WallpaperOptionDialog(
            isHomeChecked = isHomeChecked,
            isLockChecked = isLockChecked,
            onDismissRequest = { shouldShowWallpaperOptionDialog = false },
            onHomeCheckChange = { isHomeChecked = it },
            onLockCheckChange = { isLockChecked = it },
            onSetWallpaperClick = {
                val option: WallpaperOption = if (isHomeChecked && isLockChecked) {
                    WallpaperOption.HomeAndLockScreen
                } else if (isHomeChecked) {
                    WallpaperOption.HomeScreen
                } else {
                    WallpaperOption.LockScreen
                }
                viewModel.setWallpaper(option)
            }
        )
    }
    DetailsContent(
        photoDetail = uiState.photosResponse,
        isFavourite = uiState.photosResponse?.isFavourite ?: false,
        onAction = {
            when (it) {
                DetailsScreenActions.OnBackClick -> {
                    onBackClick()
                }
                DetailsScreenActions.OnDownload -> {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            viewModel.downloadImage()
                        } else {
                            launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    } else {
                        viewModel.downloadImage()
                    }
                }
                DetailsScreenActions.OnFavourite -> {
                    viewModel.togglePhotoFavourite()
                }
                DetailsScreenActions.SetAsWallpaper -> {
                    shouldShowWallpaperOptionDialog = true
                }
                DetailsScreenActions.ViewAuthorDetails -> {
                    uiState.photosResponse?.let { user ->
                        viewAuthorDetails(
                            user.id,
                            user.name,
                            user.username,
                            user.bio,
                            user.profileImage,
                            uiState.photosResponse?.blurHash.orEmpty()
                        )
                    }
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.action.collect {
            when (it) {
                is DetailsActions.ShowMessage -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsContent(
    photoDetail: DetailPhoto?,
    isFavourite: Boolean,
    onAction: (DetailsScreenActions) -> Unit
) {
    photoDetail?.let { photo ->
        val blurBitmap =
            BlurHashDecoder.decode(blurHash = photo.blurHash, width = 300, height = 300)
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState()
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 110.dp,
            sheetDragHandle = {
                Card(
                    modifier = Modifier
                        .padding(12.dp)
                        .width(100.dp)
                        .height(6.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.5F
                        )
                    )
                ) { }
            },
            sheetContent = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable { onAction(DetailsScreenActions.ViewAuthorDetails) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = photo.profileImage,
                        contentDescription = photo.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(70.dp)
                            .clip(CircleShape)
                            .border(width = 1.dp, color = Color.LightGray, shape = CircleShape),
                        placeholder = rememberAsyncImagePainter(blurBitmap),
                        error = painterResource(R.drawable.il_no_profile_image)
                    )

                    Column(
                        modifier = Modifier
                            .weight(1F)
                            .padding(start = 8.dp)
                    ) {
                        Text(text = photo.name, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(4.dp))
                        Text(text = photo.username, style = MaterialTheme.typography.bodySmall)
                    }

                    Text(
                        text = "${NumberFormat.getInstance().format(photo.likes)} likes",
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                val actualPhotoDescription = photo.description ?: photo.altDescription
                actualPhotoDescription?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AppPrimaryButton(
                        onClick = { onAction(DetailsScreenActions.SetAsWallpaper) },
                        title = "Set as wallpaper"
                    )
                    AppSecondaryButton(
                        onClick = { onAction(DetailsScreenActions.OnFavourite) },
                        title = if (isFavourite) "Remove favourites" else "Add to favourites"
                    )
                }
            },
            content = {
                Column(modifier = Modifier.fillMaxSize()) {
                    AppToolbar(
                        title = "",
                        navIcon = R.drawable.ic_arrow_back,
                        actionIcon = R.drawable.ic_download,
                        onNavIconClick = { onAction(DetailsScreenActions.OnBackClick) },
                        onActionClick = { onAction(DetailsScreenActions.OnDownload) }
                    )
                    AsyncImage(
                        model = photo.photoUrl,
                        contentDescription = photo.description,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                            },
                        placeholder = rememberAsyncImagePainter(blurBitmap)
                    )
                }
            })
    }
}

@PreviewLightDark
@Composable
fun DetailsPreview() {
    WallpaperDownloaderTheme {
        DetailsContent(photoDetail = photoItem, isFavourite = true) {}
    }
}

sealed interface DetailsScreenActions {
    data object OnFavourite : DetailsScreenActions
    data object OnDownload : DetailsScreenActions
    data object OnBackClick : DetailsScreenActions
    data object SetAsWallpaper : DetailsScreenActions
    data object ViewAuthorDetails : DetailsScreenActions
}

val photoItem = DetailPhoto(
    altDescription = "A beautiful mountain view",
    blurHash = "LKO2?U%2Tw=w]~RBVZRi};RPxuwH",
    description = "An awe-inspiring sunset behind a mountain range.",
    height = 1080,
    id = "sample-id-123",
    likes = 1502,
    width = 1920,
    isFavourite = true,
    blurHashBitmap = null,
    username = "graceo",
    name = "grace",
    thumb = "",
    profileImage = "https://example.com/profile_large.jpg",
    photoUrl = "https://example.com/regular.jpg",
    bio = "",
    downloadLocation = "https://example.com/download.jpg"
)

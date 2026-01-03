package com.segunfrancis.author.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.segunfrancis.author.domain.AuthorItem
import com.segunfrancis.theme.R
import com.segunfrancis.theme.WallpaperDownloaderTheme
import com.segunfrancis.theme.components.AppToolbar
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthorScreen(onMenuActionClick: () -> Unit, onAuthorClick: (String) -> Unit) {
    val viewModel = koinViewModel<AuthorViewModel>()
    val favourites by viewModel.favouriteAuthorsState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    AuthorContent(
        favouriteAuthors = favourites,
        onMenuActionClick = onMenuActionClick,
        onAuthorClick = onAuthorClick
    )
    LaunchedEffect(Unit) {
        viewModel.action.collect {
            when (it) {
                is AuthorActions.ShowError -> {
                    Toast.makeText(context, it.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

@Composable
fun AuthorContent(
    favouriteAuthors: List<AuthorItem>,
    onMenuActionClick: () -> Unit,
    onAuthorClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppToolbar(
            title = "Favourite Authors",
            actionIcon = R.drawable.ic_settings,
            onActionClick = {
                onMenuActionClick()
            }
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (favouriteAuthors.isEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(Modifier.height(24.dp))
                        Image(
                            painter = painterResource(R.drawable.il_no_favourites),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "No favourite authors",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                item {
                    val name = if (favouriteAuthors.count() > 1) "Authors" else "Author"
                    Text(
                        text = "${favouriteAuthors.size} $name",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 4.dp, bottom = 8.dp)
                    )
                }
                items(favouriteAuthors) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAuthorClick(it.username)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = it.profilePhoto, contentDescription = it.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                                .size(60.dp)
                                .clip(CircleShape)
                                .border(width = 1.dp, color = Color.LightGray, shape = CircleShape),
                            placeholder = painterResource(R.drawable.il_no_profile_image),
                            error = painterResource(R.drawable.il_no_profile_image)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1F)) {
                            Text(
                                text = it.name,
                                style = MaterialTheme.typography.labelMedium,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                fontSize = 18.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = it.username, style = MaterialTheme.typography.labelSmall,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                        Icon(
                            modifier = Modifier.padding(end = 16.dp, start = 8.dp),
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    WallpaperDownloaderTheme {
        AuthorContent(favouriteAuthors = emptyList(), onMenuActionClick = {}, onAuthorClick = {})
    }
}

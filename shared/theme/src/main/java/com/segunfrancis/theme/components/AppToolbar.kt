package com.segunfrancis.theme.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.segunfrancis.theme.R
import com.segunfrancis.theme.WallpaperDownloaderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppToolbar(
    modifier: Modifier = Modifier,
    title: String,
    actionIcon: Int? = null,
    onActionClick: () -> Unit = {},
    navIcon: Int? = null,
    onNavIconClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        modifier = modifier.fillMaxWidth(),
        navigationIcon = {
            navIcon?.let {
                Icon(
                    painter = painterResource(it),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onNavIconClick() }
                        .padding(8.dp)
                )
            }
        },
        actions = {
            actionIcon?.let {
                Icon(
                    painter = painterResource(it),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onActionClick() }
                        .padding(8.dp)
                )
            }
        },
        windowInsets = WindowInsets(top = 0.dp)
    )
}

@PreviewLightDark
@Composable
fun AppToolbarPreview() {
    WallpaperDownloaderTheme {
        AppToolbar(
            title = "Wallpapers",
            actionIcon = R.drawable.ic_settings,
            navIcon = R.drawable.ic_arrow_back
        )
    }
}

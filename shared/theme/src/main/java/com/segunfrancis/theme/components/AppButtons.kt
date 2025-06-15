package com.segunfrancis.theme.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.segunfrancis.theme.LocalButtonColors
import com.segunfrancis.theme.WallpaperDownloaderTheme

@Composable
fun AppPrimaryButton(modifier: Modifier = Modifier, title: String, onClick: () -> Unit) {
    val buttonColors = LocalButtonColors.current
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColors.primary,
            contentColor = buttonColors.onPrimary
        )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun AppSecondaryButton(modifier: Modifier = Modifier, title: String, onClick: () -> Unit) {
    val buttonColors = LocalButtonColors.current
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColors.secondary,
            contentColor = buttonColors.onSecondary
        )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
@PreviewLightDark
fun AppPrimaryButtonPreview() {
    WallpaperDownloaderTheme {
        AppPrimaryButton(title = "Set as wallpaper", onClick = {})
    }
}

@Composable
@PreviewLightDark
fun AppSecondaryButtonPreview() {
    WallpaperDownloaderTheme {
        AppSecondaryButton(title = "Set as wallpaper", onClick = {})
    }
}

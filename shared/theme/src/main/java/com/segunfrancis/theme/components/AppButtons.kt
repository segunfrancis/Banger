package com.segunfrancis.theme.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.segunfrancis.theme.LocalButtonColors
import com.segunfrancis.theme.WallpaperDownloaderTheme

@Composable
fun AppPrimaryButton(
    modifier: Modifier = Modifier,
    title: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val buttonColors = LocalButtonColors.current
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColors.primary,
            contentColor = buttonColors.onPrimary
        ),
        enabled = enabled
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun AppSecondaryButton(
    modifier: Modifier = Modifier,
    title: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val buttonColors = LocalButtonColors.current
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColors.secondary,
            contentColor = buttonColors.onSecondary
        ),
        enabled = enabled
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AppTextButton(modifier: Modifier = Modifier, title: String, onClick: () -> Unit) {
    val buttonColors = LocalButtonColors.current
    TextButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            contentColor = buttonColors.onSecondary,
            containerColor = buttonColors.secondary
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

@Composable
@PreviewLightDark
fun AppTextButtonPreview() {
    AppTextButton(title = "Set as wallpaper", onClick = {})
}

package com.segunfrancis.profile.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segunfrancis.theme.WallpaperDownloaderTheme

@Composable
fun ProfileScreen() {
    ProfileContent()
}

@Composable
fun ProfileContent() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Profile Screen",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    WallpaperDownloaderTheme {
        ProfileContent()
    }
}

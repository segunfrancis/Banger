package com.segunfrancis.settings.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.segunfrancis.local.AppTheme
import com.segunfrancis.local.DownloadQuality
import com.segunfrancis.theme.R
import com.segunfrancis.theme.WallpaperDownloaderTheme
import com.segunfrancis.theme.components.AppToolbar
import org.koin.androidx.compose.koinViewModel
import kotlin.enums.EnumEntries

@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    val viewModel = koinViewModel<SettingsViewModel>()
    val currentTheme by viewModel.theme.collectAsStateWithLifecycle()
    val currentDownloadQuality by viewModel.downloadQuality.collectAsStateWithLifecycle()
    SettingsContent(
        currentTheme = currentTheme,
        currentDownloadQuality = currentDownloadQuality,
        onBackClick = onBackClick,
        onThemeItemClick = { viewModel.setTheme(it) },
        onDownloadQualityItemClick = { viewModel.setDownloadQuality(it) }
    )
}

@Composable
fun SettingsContent(
    currentTheme: AppTheme,
    currentDownloadQuality: DownloadQuality,
    onBackClick: () -> Unit,
    onThemeItemClick: (AppTheme) -> Unit,
    onDownloadQualityItemClick: (DownloadQuality) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val themeOptions by remember { mutableStateOf(AppTheme.entries) }

        val downloadQualityOptions by remember { mutableStateOf(DownloadQuality.entries) }

        AppToolbar(
            title = "App Settings",
            navIcon = R.drawable.ic_arrow_back,
            onNavIconClick = { onBackClick() }
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "App Customization",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(24.dp))
        Box(modifier = Modifier.wrapContentSize()) {
            SettingsItem(
                title = "Download Quality",
                subtitle = "Choose the quality of the downloaded wallpapers",
                value = currentDownloadQuality.name,
                options = downloadQualityOptions,
                onItemClick = { onDownloadQualityItemClick(it as DownloadQuality) }
            )
        }
        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.wrapContentSize()) {
            SettingsItem(
                title = "App Theme",
                subtitle = "Choose the theme of the app",
                value = currentTheme.name,
                options = themeOptions,
                onItemClick = { onThemeItemClick(it as AppTheme) }
            )
        }
    }
}

@PreviewLightDark
@Composable
fun SettingsScreenPreview() {
    WallpaperDownloaderTheme {
        SettingsContent(
            currentTheme = AppTheme.System,
            currentDownloadQuality = DownloadQuality.Medium,
            onBackClick = {},
            onThemeItemClick = {},
            onDownloadQualityItemClick = {}
        )
    }
}

@Composable
fun SettingsItem(
    title: String = "Download Quality",
    subtitle: String = "Choose the quality of the downloaded wallpapers",
    value: String = "High",
    options: EnumEntries<*>,
    onItemClick: (Enum<*>) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1F)) {
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                options.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = value == option.name,
                        onClick = { onItemClick(option) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        )
                    ) {
                        Text(text = option.name)
                    }
                }
            }
            Text(
                text = subtitle,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

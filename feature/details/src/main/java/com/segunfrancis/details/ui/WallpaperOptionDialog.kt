package com.segunfrancis.details.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.segunfrancis.theme.components.AppPrimaryButton
import com.segunfrancis.theme.components.AppSecondaryButton

@PreviewLightDark
@Composable
fun WallpaperOptionDialog(
    isHomeChecked: Boolean = true,
    isLockChecked: Boolean = true,
    onDismissRequest: () -> Unit = {},
    onHomeCheckChange: (Boolean) -> Unit = {},
    onLockCheckChange: (Boolean) -> Unit = {},
    onSetWallpaperClick:() -> Unit = {}
) {
    AlertDialog(
        modifier = Modifier,
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Set Wallpaper on")
        },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Home Screen", modifier = Modifier.weight(1F))
                    Checkbox(checked = isHomeChecked, onCheckedChange = onHomeCheckChange)
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Lock Screen", modifier = Modifier.weight(1F))
                    Checkbox(checked = isLockChecked, onCheckedChange = onLockCheckChange)
                }
            }
        },
        confirmButton = {
            AppPrimaryButton(title = "Set Wallpaper", enabled = isHomeChecked || isLockChecked) {
                onDismissRequest()
                onSetWallpaperClick()
            }
        },
        dismissButton = {
            AppSecondaryButton(title = "Cancel") {
                onDismissRequest()
            }
        }
    )
}

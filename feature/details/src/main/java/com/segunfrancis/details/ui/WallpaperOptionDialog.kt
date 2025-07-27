package com.segunfrancis.details.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
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
    onSetWallpaperClick: () -> Unit = {}
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = isHomeChecked,
                            role = Role.Checkbox,
                            onValueChange = onHomeCheckChange
                        )
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Home Screen",
                        modifier = Modifier
                            .weight(1F)
                            .padding(horizontal = 4.dp)
                    )
                    Checkbox(checked = isHomeChecked, onCheckedChange = null)
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = isLockChecked,
                            role = Role.Checkbox,
                            onValueChange = onLockCheckChange
                        )
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lock Screen",
                        modifier = Modifier
                            .weight(1F)
                            .padding(horizontal = 4.dp)
                    )
                    Checkbox(checked = isLockChecked, onCheckedChange = null)
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

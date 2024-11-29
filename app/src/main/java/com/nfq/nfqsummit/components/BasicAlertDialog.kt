package com.nfq.nfqsummit.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun BasicAlertDialog(
    title: String = "",
    body: String = "",
    onDismissRequest: () -> Unit = {},
    confirmButtonText: String = "Okay",
    confirmButton: () -> Unit,
    dismissButtonText: String = "Cancel",
    dismissButton: (() -> Unit)? = null,
    properties: DialogProperties = DialogProperties()
) {
    AlertDialog(
        properties = properties,
        tonalElevation = 0.dp,
        title = {
            if (title.isNotBlank()) {
                Text(text = title)
            }
        },
        text = {
            if (body.isNotBlank()) {
                Text(text = body)
            }
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            if (dismissButton != null) {
                TextButton(
                    onClick = dismissButton,
                    content = {
                        Text(dismissButtonText)
                    }
                )
            }
        },

        confirmButton = {
            TextButton(
                onClick = confirmButton,
                content = {
                    Text(confirmButtonText)
                }
            )
        }
    )
}

@Preview
@Composable
private fun BasicAlertDialogPreview() {
    NFQSnapshotTestThemeForPreview {
        BasicAlertDialog(
            title = "Error",
            body = "An error occurred and please try again later.",
            confirmButton = { }
        )
    }
}


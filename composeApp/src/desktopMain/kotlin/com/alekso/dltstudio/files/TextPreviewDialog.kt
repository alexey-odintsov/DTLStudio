package com.alekso.dltstudio.files

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow


data class TextPreviewDialogState(
    val showDialog: Boolean = false,
    val fileEntry: FileEntry? = null,
)

@Composable
fun TextPreviewDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    fileEntry: FileEntry,
) {
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = fileEntry.name,
        state = DialogState(width = 600.dp, height = 520.dp)
    ) {
        Text(String(fileEntry.getContent() ?: byteArrayOf()))
    }
}
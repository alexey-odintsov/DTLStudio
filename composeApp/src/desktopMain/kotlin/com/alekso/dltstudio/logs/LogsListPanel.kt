package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import com.alekso.dltstudio.LogRemoveContext
import com.alekso.dltstudio.RowContextMenuCallbacks
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.ui.Panel
import com.alekso.dltstudio.utils.SampleData

@Composable
fun LogsListPanel(
    modifier: Modifier = Modifier,
    messages: SnapshotStateList<LogMessage>,
    colorFilters: List<ColorFilter>,
    selectedRow: Int,
    logsListState: LazyListState,
    onLogsRowSelected: (Int, Int) -> Unit,
    wrapContent: Boolean,
    rowContextMenuCallbacks: RowContextMenuCallbacks,
    showComments: Boolean,
) {
    Panel(
        modifier = modifier,
        title = "Messages"
    ) {
        LazyScrollable(
            Modifier.fillMaxSize().background(Color.LightGray),
            messages,
            null,
            colorFilters,
            selectedRow = selectedRow,
            onRowSelected = onLogsRowSelected,
            listState = logsListState,
            wrapContent = wrapContent,
            rowContextMenuCallbacks = rowContextMenuCallbacks,
            showComments = showComments,
        )
    }
}

@Preview
@Composable
fun PreviewLogsListPanel() {
    val list = SnapshotStateList<LogMessage>()
    list.addAll(
        SampleData.getSampleDltMessages(20)
            .map { LogMessage(dltMessage = it, marked = true, comment = "Test comment") })

    LogsListPanel(
        modifier = Modifier.fillMaxSize(),
        messages = list,
        colorFilters = emptyList(),
        selectedRow = 1,
        logsListState = LazyListState(),
        onLogsRowSelected = { _, _ -> },
        wrapContent = true,
        rowContextMenuCallbacks = object: RowContextMenuCallbacks {
            override fun onCopyClicked(text: AnnotatedString) = Unit
            override fun onMarkClicked(i: Int, message: LogMessage) = Unit
            override fun onRemoveClicked(context: LogRemoveContext, filter: String) = Unit
            override fun onRemoveDialogClicked(message: LogMessage) = Unit
        },
        showComments = true,
    )
}
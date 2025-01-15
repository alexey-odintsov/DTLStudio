package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.LogRemoveContext
import com.alekso.dltstudio.RowContextMenuCallbacks
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.logs.colorfilters.ColorFilterError
import com.alekso.dltstudio.logs.colorfilters.ColorFilterFatal
import com.alekso.dltstudio.logs.colorfilters.ColorFilterWarn
import com.alekso.dltstudio.logs.infopanel.LogPreviewPanel
import com.alekso.dltstudio.logs.insights.LogInsight
import com.alekso.dltstudio.logs.search.SearchState
import com.alekso.dltstudio.logs.search.SearchType
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.model.VirtualDevice
import com.alekso.dltstudio.utils.SampleData
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.SplitPaneState
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.skiko.Cursor


private fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

private fun Modifier.cursorForVerticalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.S_RESIZE_CURSOR)))

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun LogsPanel(
    modifier: Modifier = Modifier,
    logMessages: SnapshotStateList<LogMessage>,
    logInsights: SnapshotStateList<LogInsight>? = null,
    virtualDevices: List<VirtualDevice>,
    // search
    searchState: SearchState,
    searchResult: SnapshotStateList<LogMessage>,
    searchIndexes: List<Int>,
    searchAutoComplete: List<String>,
    // color filters
    colorFilters: List<ColorFilter>,
    // toolbar
    logsToolbarState: LogsToolbarState,
    logsToolbarCallbacks: LogsToolbarCallbacks,
    // split bar
    vSplitterState: SplitPaneState,
    hSplitterState: SplitPaneState,
    logsListState: LazyListState,
    searchListState: LazyListState,
    onLogsRowSelected: (Int, Int) -> Unit,
    onSearchRowSelected: (Int, Int) -> Unit,
    logsListSelectedRow: Int,
    searchListSelectedRow: Int,
    rowContextMenuCallbacks: RowContextMenuCallbacks,
    onCommentUpdated: (LogMessage, String?) -> Unit = { _, _ -> },
    onShowVirtualDeviceClicked: () -> Unit = {},
) {

    Column(modifier = modifier) {
        LogsToolbar(
            logsToolbarState,
            searchState,
            searchAutoComplete,
            callbacks = logsToolbarCallbacks,
        )

        Divider()
        val mergedFilters = mutableListOf<ColorFilter>()
        mergedFilters.addAll(colorFilters)
        if (logsToolbarState.toolbarWarningChecked) {
            mergedFilters.add(ColorFilterWarn)
        }
        if (logsToolbarState.toolbarErrorChecked) {
            mergedFilters.add(ColorFilterError)
        }
        if (logsToolbarState.toolbarFatalChecked) {
            mergedFilters.add(ColorFilterFatal)
        }


        VerticalSplitPane(splitPaneState = vSplitterState) {
            first(50.dp) {
                HorizontalSplitPane(
                    splitPaneState = hSplitterState
                ) {
                    first(20.dp) {
                        LogsListPanel(
                            Modifier.fillMaxSize(),
                            logMessages,
                            mergedFilters,
                            logsListSelectedRow,
                            logsListState = logsListState,
                            onLogsRowSelected = onLogsRowSelected,
                            wrapContent = logsToolbarState.toolbarWrapContentChecked,
                            showComments = logsToolbarState.toolbarCommentsChecked,
                            rowContextMenuCallbacks = rowContextMenuCallbacks,
                        )
                    }
                    second(20.dp) {
                        LogPreviewPanel(
                            Modifier.fillMaxSize(),
                            logMessages.getOrNull(logsListSelectedRow),
                            logInsights = logInsights,
                            virtualDevices = virtualDevices,
                            messageIndex = logsListSelectedRow,
                            onShowVirtualDeviceClicked = onShowVirtualDeviceClicked,
                            onCommentUpdated = onCommentUpdated,
                        )
                    }
                    splitter {
                        visiblePart {
                            Box(
                                Modifier
                                    .width(1.dp)
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colors.background)
                            )
                        }
                        handle {
                            Box(
                                Modifier
                                    .markAsHandle()
                                    .cursorForHorizontalResize()
                                    .background(SolidColor(Color.Gray), alpha = 0.50f)
                                    .width(4.dp)
                                    .fillMaxHeight()
                            )
                        }
                    }
                }
            }
            second(20.dp) {
                SearchResultsPanel(
                    Modifier.fillMaxSize(),
                    searchResult,
                    searchIndexes,
                    mergedFilters,
                    searchListSelectedRow,
                    searchListState = searchListState,
                    onSearchRowSelected = onSearchRowSelected,
                    wrapContent = logsToolbarState.toolbarWrapContentChecked,
                    showComments = logsToolbarState.toolbarCommentsChecked,
                    rowContextMenuCallbacks = rowContextMenuCallbacks,
                )
            }
            splitter {
                visiblePart {
                    Box(
                        Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.background)
                    )
                }
                handle {
                    Box(
                        Modifier
                            .markAsHandle()
                            .cursorForVerticalResize()
                            .background(SolidColor(Color.Gray), alpha = 0.50f)
                            .height(4.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSplitPaneApi::class)
@Preview
@Composable
fun PreviewLogsPanel() {
    val list = SnapshotStateList<LogMessage>()
    list.addAll(SampleData.getSampleDltMessages(20).map { LogMessage(it) })
    LogsPanel(
        Modifier.fillMaxSize(),
        logMessages = list,
        searchState = SearchState(searchText = "Search text"),
        searchResult = SnapshotStateList(),
        searchIndexes = emptyList(),
        colorFilters = emptyList(),
        logsToolbarState = LogsToolbarState(
            toolbarFatalChecked = true,
            toolbarErrorChecked = true,
            toolbarWarningChecked = true,
            toolbarWrapContentChecked = true,
            toolbarCommentsChecked = false,
        ),
        logsToolbarCallbacks = object : LogsToolbarCallbacks {
            override fun onSearchButtonClicked(searchType: SearchType, text: String) {

            }

            override fun updateToolbarFatalCheck(checked: Boolean) {

            }

            override fun updateToolbarErrorCheck(checked: Boolean) {

            }

            override fun updateToolbarWarningCheck(checked: Boolean) {

            }

            override fun updateToolbarCommentsCheck(checked: Boolean) {

            }

            override fun updateToolbarWrapContentCheck(checked: Boolean) {

            }

            override fun onSearchUseRegexChanged(checked: Boolean) {

            }

            override fun onColorFiltersClicked() {

            }
        },
        vSplitterState = SplitPaneState(0.8f, true),
        hSplitterState = SplitPaneState(0.8f, true),
        logsListState = LazyListState(),
        searchListState = LazyListState(),
        onLogsRowSelected = { i, r -> },
        onSearchRowSelected = { i, r -> },
        logsListSelectedRow =0,
        searchListSelectedRow = 0,
        searchAutoComplete = emptyList(),
        virtualDevices = emptyList(),
        rowContextMenuCallbacks = object : RowContextMenuCallbacks {
            override fun onCopyClicked(text: AnnotatedString) = Unit
            override fun onMarkClicked(i: Int, message: LogMessage) = Unit
            override fun onRemoveClicked(context: LogRemoveContext, filter: String) = Unit
            override fun onRemoveDialogClicked(message: LogMessage) = Unit
        },
    )
}
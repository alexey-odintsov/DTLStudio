package com.alekso.dltstudio.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.alekso.dltparser.DLTParserV1
import com.alekso.dltstudio.LogRemoveContext
import com.alekso.dltstudio.MainViewModel
import com.alekso.dltstudio.RowContextMenuCallbacks
import com.alekso.dltstudio.db.virtualdevice.VirtualDeviceMock
import com.alekso.dltstudio.device.analyse.DeviceAnalysePanel
import com.alekso.dltstudio.device.analyse.DeviceAnalyzeViewModel
import com.alekso.dltstudio.logs.LogsPanel
import com.alekso.dltstudio.logs.LogsToolbarCallbacks
import com.alekso.dltstudio.logs.LogsToolbarState
import com.alekso.dltstudio.logs.RemoveLogsDialog
import com.alekso.dltstudio.logs.RemoveLogsDialogState
import com.alekso.dltstudio.logs.colorfilters.ColorFiltersDialog
import com.alekso.dltstudio.logs.infopanel.VirtualDevicesDialog
import com.alekso.dltstudio.logs.insights.InsightsRepository
import com.alekso.dltstudio.logs.search.SearchType
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.timeline.TimeLinePanel
import com.alekso.dltstudio.timeline.TimelineViewModel
import dltstudio.composeapp.generated.resources.Res
import dltstudio.composeapp.generated.resources.tab_logs
import dltstudio.composeapp.generated.resources.tab_timeline
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.io.File
import java.net.URI


@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalSplitPaneApi::class,
    ExperimentalFoundationApi::class
)
@Composable
@Preview
fun MainWindow(
    mainViewModel: MainViewModel,
    timelineViewModel: TimelineViewModel,
    deviceAnalyzeViewModel: DeviceAnalyzeViewModel,
    progress: Float,
    onProgressUpdate: (Float) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    var tabIndex by remember { mutableStateOf(0) }
    var offset by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }

    val vSplitterState = rememberSplitPaneState(0.8f)
    val hSplitterState = rememberSplitPaneState(0.78f)

    var logsToolbarState by remember {
        mutableStateOf(
            LogsToolbarState(
                toolbarFatalChecked = true,
                toolbarErrorChecked = true,
                toolbarWarningChecked = true,
                toolbarWrapContentChecked = false,
                toolbarCommentsChecked = false,
            )
        )
    }

    val tabClickListener: (Int) -> Unit = { i -> tabIndex = i }
    val offsetUpdateCallback: (Float) -> Unit = { newOffset -> offset = newOffset }
    val scaleUpdateCallback: (Float) -> Unit =
        { newScale -> scale = if (newScale > 0f) newScale else 1f }

    // Logs toolbar
    val searchState by mainViewModel.searchState.collectAsState()
    val colorFiltersDialogState = remember { mutableStateOf(false) }

    if (colorFiltersDialogState.value) {
        ColorFiltersDialog(
            visible = colorFiltersDialogState.value,
            onDialogClosed = { colorFiltersDialogState.value = false },
            colorFilters = mainViewModel.colorFilters,
            onColorFilterUpdate = { i, f -> mainViewModel.onColorFilterUpdate(i, f) },
            onColorFilterDelete = { mainViewModel.onColorFilterDelete(it) },
            onColorFilterMove = { i, o -> mainViewModel.onColorFilterMove(i, o) },
        )
    }

    val devicePreviewsDialogState = remember { mutableStateOf(false) }

    if (devicePreviewsDialogState.value) {
        VirtualDevicesDialog(
            visible = devicePreviewsDialogState.value,
            onDialogClosed = { devicePreviewsDialogState.value = false },
            virtualDevices = mainViewModel.virtualDevices,
            onVirtualDeviceUpdate = { device -> mainViewModel.onVirtualDeviceUpdate(device) },
            onVirtualDeviceDelete = { device -> mainViewModel.onVirtualDeviceDelete(device) },
        )
    }

    val removeLogsDialogState = remember {
        mutableStateOf(
            RemoveLogsDialogState(
                visible = false,
                message = null
            )
        )
    }

    if (removeLogsDialogState.value.visible) {
        RemoveLogsDialog(
            visible = removeLogsDialogState.value.visible,
            message = removeLogsDialogState.value.message,
            onDialogClosed = { removeLogsDialogState.value = RemoveLogsDialogState(false) },
            onFilterClicked = { f -> mainViewModel.removeMessagesByFilters(f) },
        )
    }

    val logsToolbarCallbacks = object : LogsToolbarCallbacks {
        override fun onSearchButtonClicked(searchType: SearchType, text: String) {
            mainViewModel.onSearchClicked(searchType, text)
        }

        override fun updateToolbarFatalCheck(checked: Boolean) {
            logsToolbarState = LogsToolbarState.updateToolbarFatalCheck(logsToolbarState, checked)
        }

        override fun updateToolbarErrorCheck(checked: Boolean) {
            logsToolbarState = LogsToolbarState.updateToolbarErrorCheck(logsToolbarState, checked)
        }

        override fun updateToolbarWarningCheck(checked: Boolean) {
            logsToolbarState = LogsToolbarState.updateToolbarWarnCheck(logsToolbarState, checked)
        }

        override fun updateToolbarCommentsCheck(checked: Boolean) {
            logsToolbarState =
                LogsToolbarState.updateToolbarCommentsCheck(logsToolbarState, checked)
        }

        override fun updateToolbarWrapContentCheck(checked: Boolean) {
            logsToolbarState =
                LogsToolbarState.updateToolbarWrapContentCheck(logsToolbarState, checked)
        }

        override fun onSearchUseRegexChanged(checked: Boolean) {
            mainViewModel.onSearchUseRegexChanged(checked)
        }

        override fun onColorFiltersClicked() {
            colorFiltersDialogState.value = true
        }
    }

    Column(
        modifier = Modifier.dragAndDropTarget(
            shouldStartDragAndDrop = {
                true
            },
            target = object : DragAndDropTarget {
                override fun onDrop(event: DragAndDropEvent): Boolean {
                    if (event.dragData() is DragData.FilesList) {
                        val dragFilesList = event.dragData() as DragData.FilesList
                        val pathList = dragFilesList.readFiles()

                        if (pathList.isNotEmpty()) {
                            val filesList =
                                pathList.map { path -> File(URI.create(path.substring(5)).path) }
                            mainViewModel.parseFile(filesList)
                        }
                        return true
                    }
                    return false
                }
            }))
        {
            TabsPanel(
                tabIndex,
                listOf(
                    stringResource(Res.string.tab_logs),
                    stringResource(Res.string.tab_timeline),
                    "Device Analyse",
                    ),
                tabClickListener
            )

            when (tabIndex) {
                0 -> {
                    LogsPanel(
                        modifier = Modifier.weight(1f),
                        searchState = searchState,
                        searchAutoComplete = mainViewModel.searchAutocomplete,
                        logMessages = mainViewModel.logMessages,
                        logInsights = mainViewModel.logInsights,
                        virtualDevices = mainViewModel.virtualDevices,
                        searchResult = mainViewModel.searchResult,
                        searchIndexes = mainViewModel.searchIndexes,
                        colorFilters = mainViewModel.colorFilters,
                        logsToolbarState = logsToolbarState,
                        logsToolbarCallbacks = logsToolbarCallbacks,
                        vSplitterState = vSplitterState,
                        hSplitterState = hSplitterState,
                        logsListState = mainViewModel.logsListState,
                        logsListSelectedRow = mainViewModel.logsListSelectedRow.value,
                        searchListSelectedRow = mainViewModel.searchListSelectedRow.value,
                        searchListState = mainViewModel.searchListState,
                        onLogsRowSelected = { i, r ->
                            mainViewModel.onLogsRowSelected(
                                coroutineScope,
                                i,
                                r
                            )
                        },
                        onSearchRowSelected = { i, r ->
                            mainViewModel.onSearchRowSelected(
                                coroutineScope,
                                i,
                                r
                            )
                        },
                        onCommentUpdated = { logMessage, comment ->
                            mainViewModel.updateComment(
                                logMessage,
                                comment
                            )
                        },
                        rowContextMenuCallbacks = object : RowContextMenuCallbacks {
                            override fun onCopyClicked(text: AnnotatedString) {
                                clipboardManager.setText(text)
                            }

                            override fun onMarkClicked(i: Int, message: LogMessage) {
                                mainViewModel.markMessage(i, message)
                            }

                            override fun onRemoveClicked(
                                context: LogRemoveContext,
                                filter: String
                            ) {
                                mainViewModel.removeMessages(context, filter)
                            }

                            override fun onRemoveDialogClicked(message: LogMessage) {
                                removeLogsDialogState.value =
                                    RemoveLogsDialogState(true, message)
                            }
                        },
                        onShowVirtualDeviceClicked = {
                            devicePreviewsDialogState.value = true
                        }
                    )
                }

                1 -> TimeLinePanel(
                    modifier = Modifier.weight(1f),
                    timelineViewModel = timelineViewModel,
                    mainViewModel.logMessages,
                    offset,
                    offsetUpdateCallback,
                    scale,
                    scaleUpdateCallback
                )

                2 -> DeviceAnalysePanel(
                    modifier = Modifier.weight(1f),
                    deviceAnalyzeViewModel = deviceAnalyzeViewModel
                )
            }
            Divider()
            val statusText = if (mainViewModel.logMessages.isNotEmpty()) {
                "Messages: ${"%,d".format(mainViewModel.logMessages.size)}"
            } else {
                "No file loaded"
            }
            StatusBar(modifier = Modifier.fillMaxWidth(), progress, statusText)
        }
}

@Preview
@Composable
fun PreviewMainWindow() {
    Box(modifier = Modifier.width(400.dp).height(500.dp)) {
        MainWindow(
            MainViewModel(
                DLTParserV1(),
                {},
                insightsRepository = InsightsRepository(),
                virtualDeviceRepository = VirtualDeviceMock()
            ),
            TimelineViewModel({}),
            DeviceAnalyzeViewModel({}),
            1f,
            {})
    }
}
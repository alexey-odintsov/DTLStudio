import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.alekso.dltparser.DLTParserV2
import com.alekso.dltstudio.AppTheme
import com.alekso.dltstudio.MainViewModel
import com.alekso.dltstudio.db.DBFactory
import com.alekso.dltstudio.db.virtualdevice.VirtualDeviceRepository
import com.alekso.dltstudio.db.virtualdevice.VirtualDeviceRepositoryImpl
import com.alekso.dltstudio.device.analyse.DeviceAnalyzeViewModel
import com.alekso.dltstudio.logs.insights.InsightsRepository
import com.alekso.dltstudio.preferences.Preferences
import com.alekso.dltstudio.timeline.TimelineViewModel
import com.alekso.dltstudio.ui.FileChooserDialog
import com.alekso.dltstudio.ui.FileChooserDialogState
import com.alekso.dltstudio.ui.MainWindow
import com.alekso.logger.Log
import dltstudio.composeapp.generated.resources.Res
import dltstudio.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File

fun main() = application {
    Log.r("===================")
    Log.d("Application started")

    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        Log.e("Uncaught exception occurred in $thread: $throwable\n${throwable.stackTraceToString()}")
    }
    Preferences.loadFromFile()
    Window(
        onCloseRequest = {
            Preferences.saveToFile()
            Log.d("Application closed")
            exitApplication()
        },
        title = stringResource(Res.string.app_name),
        state = WindowState(width = 1280.dp, height = 768.dp)
    ) {

        AppTheme {
            var progress by remember { mutableStateOf(0f) }
            val onProgressUpdate: (Float) -> Unit = { i -> progress = i }

            val virtualDeviceRepository: VirtualDeviceRepository by lazy {
                VirtualDeviceRepositoryImpl(
                    database = DBFactory().createDatabase(),
                    scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
                )
            }

            val mainViewModel = remember {
                MainViewModel(
                    dltParser = DLTParserV2(),
                    insightsRepository = InsightsRepository(),
                    onProgressChanged = onProgressUpdate,
                    virtualDeviceRepository = virtualDeviceRepository,
                )
            }
            val timelineViewModel = remember { TimelineViewModel(onProgressUpdate) }
            val deviceAnalyzeViewModel = remember { DeviceAnalyzeViewModel(onProgressUpdate) }

            var stateIOpenFileDialog by remember { mutableStateOf(FileChooserDialogState()) }

            MenuBar {
                Menu("File") {
                    Item(
                        "Open",
                        onClick = {
                            stateIOpenFileDialog = FileChooserDialogState(
                                true,
                                FileChooserDialogState.DialogContext.OPEN_DLT_FILE
                            )
                        })
                }
                Menu("Color filters") {
                    Preferences.recentColorFilters().forEach {
                        Item(
                            it.fileName,
                            onClick = {
                                mainViewModel.loadColorFilters(File(it.absolutePath))
                            })
                    }
                    if (Preferences.recentColorFilters().isNotEmpty()) {
                        Separator()
                    }

                    Item(
                        "Open",
                        onClick = {
                            stateIOpenFileDialog = FileChooserDialogState(
                                true,
                                FileChooserDialogState.DialogContext.OPEN_FILTER_FILE
                            )
                        })
                    Item(
                        "Save",
                        onClick = {
                            stateIOpenFileDialog = FileChooserDialogState(
                                true,
                                FileChooserDialogState.DialogContext.SAVE_FILTER_FILE
                            )
                        })
                    Item(
                        "Clear",
                        onClick = { mainViewModel.clearColorFilters() })
                }
                Menu("Timeline") {
                    Menu("Filters") {
                        Preferences.recentTimelineFilters().forEach {
                            Item(
                                it.fileName,
                                onClick = {
                                    timelineViewModel.loadTimeLineFilters(File(it.absolutePath))
                                })
                        }
                        if (Preferences.recentTimelineFilters().isNotEmpty()) {
                            Separator()
                        }
                        Item(
                            "Open",
                            onClick = {
                                stateIOpenFileDialog = FileChooserDialogState(
                                    true,
                                    FileChooserDialogState.DialogContext.OPEN_TIMELINE_FILTER_FILE
                                )
                            })
                        Item(
                            "Save",
                            onClick = {
                                stateIOpenFileDialog = FileChooserDialogState(
                                    true,
                                    FileChooserDialogState.DialogContext.SAVE_TIMELINE_FILTER_FILE
                                )
                            })
                        Item("Clear", onClick = { timelineViewModel.clearTimeLineFilters() })
                    }
                }
            }

            if (stateIOpenFileDialog.visibility) {
                FileChooserDialog(
                    dialogContext = stateIOpenFileDialog.dialogContext,
                    title = when (stateIOpenFileDialog.dialogContext) {
                        FileChooserDialogState.DialogContext.OPEN_DLT_FILE -> "Open DLT file"
                        FileChooserDialogState.DialogContext.OPEN_FILTER_FILE -> "Open filters"
                        FileChooserDialogState.DialogContext.UNKNOWN -> "Open file"
                        FileChooserDialogState.DialogContext.SAVE_FILTER_FILE -> "Save filter"
                        FileChooserDialogState.DialogContext.OPEN_TIMELINE_FILTER_FILE -> "Open TimeLine filters"
                        FileChooserDialogState.DialogContext.SAVE_TIMELINE_FILTER_FILE -> "Save TimeLine filters"
                    },
                    onFileSelected = { file ->
                        when (stateIOpenFileDialog.dialogContext) {
                            FileChooserDialogState.DialogContext.OPEN_DLT_FILE -> {
                                file?.let {
                                    mainViewModel.parseFile(listOf(it))
                                }
                            }

                            FileChooserDialogState.DialogContext.OPEN_FILTER_FILE -> {
                                file?.let {
                                    mainViewModel.loadColorFilters(it)
                                }
                            }

                            FileChooserDialogState.DialogContext.SAVE_FILTER_FILE -> {
                                file?.let {
                                    mainViewModel.saveColorFilters(it)
                                }
                            }

                            FileChooserDialogState.DialogContext.OPEN_TIMELINE_FILTER_FILE -> {
                                file?.let {
                                    timelineViewModel.loadTimeLineFilters(it)
                                }
                            }

                            FileChooserDialogState.DialogContext.SAVE_TIMELINE_FILTER_FILE -> {
                                file?.let {
                                    timelineViewModel.saveTimeLineFilters(it)
                                }
                            }

                            FileChooserDialogState.DialogContext.UNKNOWN -> {

                            }
                        }
                        stateIOpenFileDialog = stateIOpenFileDialog.copy(visibility = false)
                    },
                )
            }

            MainWindow(
                mainViewModel,
                timelineViewModel,
                deviceAnalyzeViewModel,
                progress,
                onProgressUpdate
            )
        }
    }
}
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
import com.alekso.dltstudio.MainViewModel
import com.alekso.dltstudio.logs.insights.InsightsRepository
import com.alekso.dltstudio.preferences.Preferences
import com.alekso.dltstudio.timeline.TimelineViewModel
import com.alekso.dltstudio.ui.FileChooserDialog
import com.alekso.dltstudio.ui.FileChooserDialogState
import com.alekso.dltstudio.ui.MainWindow
import com.alekso.logger.Log
import java.io.File

fun main() = application {
    Log.r("===================")
    Log.d("Application started")
    Preferences.loadFromFile()
    Window(
        onCloseRequest = {
            Preferences.saveToFile()
            Log.d("Application closed")
            exitApplication()
        },
        title = "DTL Studio",
        state = WindowState(width = 1280.dp, height = 768.dp)
    ) {
//        MaterialTheme(
//            colors = MaterialTheme.colors,
//            typography = MaterialTheme.typography, // todo: Default font size is too big
//            shapes = MaterialTheme.shapes
//        ) {


        var progress by remember { mutableStateOf(0f) }
        val onProgressUpdate: (Float) -> Unit = { i -> progress = i }


        val mainViewModel = remember { MainViewModel(
            dltParser = DLTParserV2(),
            insightsRepository = InsightsRepository(),
            onProgressChanged = onProgressUpdate,
        ) }
        val timelineViewModel = remember { TimelineViewModel(onProgressUpdate) }

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

        MainWindow(mainViewModel, timelineViewModel, progress, onProgressUpdate)
//        }
    }
}
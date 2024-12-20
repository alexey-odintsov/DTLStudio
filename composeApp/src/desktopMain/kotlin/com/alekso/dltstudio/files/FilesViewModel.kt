package com.alekso.dltstudio.files

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import com.alekso.dltstudio.model.LogMessage
import com.alekso.logger.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap

enum class FilesState {
    IDLE,
    ANALYZING
}

abstract class PreviewState(
    open val type: Type = Type.None,
    open val entry: FileEntry? = null,
) {
    enum class Type {
        None,
        Text,
        Image,
    }
}

data class TextPreviewState(override val entry: FileEntry) : PreviewState(Type.Text, entry)
data class ImagePreviewState(override val entry: FileEntry, val imageBitmap: ImageBitmap) :
    PreviewState(Type.Image, entry)


private const val PROGRESS_UPDATE_DEBOUNCE_MS = 30

class FilesViewModel(
    private val onProgressChanged: (Float) -> Unit
) {

    private var _previewState: MutableState<PreviewState?> = mutableStateOf(null)
    val previewState: State<PreviewState?> = _previewState

    private var analyzeJob: Job? = null

    var filesEntriesMap = mutableStateMapOf<Long, FileEntry>()

    private var _analyzeState: MutableState<FilesState> = mutableStateOf(FilesState.IDLE)
    val analyzeState: State<FilesState> = _analyzeState

    private fun cleanup() {
        filesEntriesMap.clear()
    }

    fun startFilesSearch(logMessages: List<LogMessage>) {
        when (_analyzeState.value) {
            FilesState.IDLE -> startAnalyzing(logMessages)
            FilesState.ANALYZING -> stopAnalyzing()
        }

    }

    private fun stopAnalyzing() {
        analyzeJob?.cancel()
        _analyzeState.value = FilesState.IDLE
    }

    fun startAnalyzing(dltMessages: List<LogMessage>) {
        cleanup()
        _analyzeState.value = FilesState.ANALYZING
        analyzeJob = CoroutineScope(Dispatchers.IO).launch {
            val start = System.currentTimeMillis()
            if (dltMessages.isNotEmpty()) {
                val fileExtractor = FileExtractor()

                Log.d("Start Files analyzing .. ${dltMessages.size} messages")

                var prevTs = System.currentTimeMillis()
                dltMessages.forEachIndexed { index, message ->
                    yield()

                    try {
                        fileExtractor.searchForFiles(message.dltMessage.payload)
                    } catch (e: Exception) {
                        Log.e(e.toString())
                    }
                    val nowTs = System.currentTimeMillis()
                    if (nowTs - prevTs > PROGRESS_UPDATE_DEBOUNCE_MS) {
                        prevTs = nowTs
                        onProgressChanged(index.toFloat() / dltMessages.size)
                    }
                }

                withContext(Dispatchers.Default) {
                    // we need copies of ParseSession's collections to prevent ConcurrentModificationException
                    filesEntriesMap.clear()
                    filesEntriesMap.putAll(fileExtractor.filesMap)
                    _analyzeState.value = FilesState.IDLE
                }
                onProgressChanged(1f)
            }
            Log.d("Done analyzing files ${System.currentTimeMillis() - start}ms")
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    fun onFileClicked(entry: FileEntry) {
        CoroutineScope(Dispatchers.IO).launch {
            println("On file clicked ${entry.name}")

            when (entry.getExtension()) {
                "png" -> {
                    val bytes = entry.getContent()
                    if (bytes != null) {
                        _previewState.value = ImagePreviewState(
                            entry = entry,
                            imageBitmap = bytes.decodeToImageBitmap()
                        )
                    }
                }

                "txt" -> {
                    _previewState.value = TextPreviewState(entry = entry)
                }

                else -> {
                    _previewState.value = null
                }
            }
        }
    }

    fun closePreviewDialog() {
        _previewState.value = null
    }
}

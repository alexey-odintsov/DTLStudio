package com.alekso.dltstudio.ui

import androidx.compose.runtime.mutableStateListOf
import com.alekso.dltparser.DLTParser
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.ui.cpu.CPUSEntry
import com.alekso.dltstudio.ui.cpu.CPUUsageEntry
import com.alekso.dltstudio.ui.memory.MemoryUsageEntry
import java.io.File

class ParseSession(private val progressCallback: (Float) -> Unit, val file: File) {
    val dltMessages = mutableStateListOf<DLTMessage>()
    var cpuUsage = mutableListOf<CPUUsageEntry>()
    var cpus = mutableListOf<CPUSEntry>()
    var memt = mutableMapOf<String, MutableList<MemoryUsageEntry>>()
    var timeStart = Long.MAX_VALUE
    var timeEnd = Long.MIN_VALUE
    var totalSeconds: Int = 0

    suspend fun start() {
        dltMessages.addAll(DLTParser.read(progressCallback, file.inputStream()))
    }
}
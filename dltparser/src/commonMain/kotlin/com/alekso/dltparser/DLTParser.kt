package com.alekso.dltparser

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.PayloadStorageType
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


interface DLTParser {
    val payloadStorageType: PayloadStorageType

    companion object {
        const val SIGNATURE_D = 'D'.code.toByte()
        const val SIGNATURE_L = 'L'.code.toByte()
        const val SIGNATURE_T = 'T'.code.toByte()
        const val SIGNATURE_01 = 1.toByte()
        const val DEBUG_LOG = true // WARNING: Logging drastically slow down parsing!!!
        const val MAX_BYTES_TO_READ_DEBUG = -1 // put -1 to ignore
        const val DLT_HEADER_SIZE_BYTES = 16
        const val STRING_CODING_MASK = 0b00000000000000000000000000000111
        val STANDARD_HEADER_ENDIAN = Endian.BIG
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
    }

    suspend fun read(
        progressCallback: (Float) -> Unit, files: List<File>
    ): List<DLTMessage>

}
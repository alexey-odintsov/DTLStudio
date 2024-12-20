package com.alekso.dltstudio.files

import androidx.compose.runtime.mutableStateMapOf


data class FileEntry(
    var serialNumber: Long = 0L,
    var name: String = "",
    var size: Long = 0L,
    var creationDate: String = "",
    var numberOfPackages: Int = 0,
    var bufferSize: Int = 0,
    var bytes: Array<ByteArray>? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileEntry

        if (serialNumber != other.serialNumber) return false
        if (size != other.size) return false
        if (numberOfPackages != other.numberOfPackages) return false
        if (bufferSize != other.bufferSize) return false
        if (name != other.name) return false
        if (creationDate != other.creationDate) return false
        if (!bytes.contentDeepEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = serialNumber.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + numberOfPackages
        result = 31 * result + bufferSize
        result = 31 * result + name.hashCode()
        result = 31 * result + creationDate.hashCode()
        result = 31 * result + (bytes?.contentDeepHashCode() ?: 0)
        return result
    }


    fun getContent(): ByteArray? {
        return bytes?.flatMap { it.asList() }?.toByteArray()
    }

    fun getExtension(): String {
        return name.substringAfterLast(".")
    }
}

/**
 * The base file structure is as follow:
 * ```
 * FLIF file serial number {serial_number} filename {file_name} file size in byte {file_size} file creation date {date_string} number of packages {packages_num} FLIF
 *    FLST {serial_number} {file_name} {file_size} {date_string} {packages_num} {buffer_size} FLST
 *    FLDA {serial_number} {package_num} {buffer_bytes} FLDA
 *    FLDA {serial_number} {package_num} {buffer_bytes} FLDA
 *    ..
 *    FLDA {serial_number} {package_num} {buffer_bytes} FLDA
 * FLFI
 * ```
 */
class FileExtractor {
    val filesMap = mutableStateMapOf<Long, FileEntry>()

    private val flifRegexp =
        """FLIF file serialnumber (?<serial>\d+)\sfilename (?<name>.*?) file size in bytes (?<size>\d+).*creation date (?<creationdate>.*?) number of packages (?<numpackages>\d+)\sFLIF""".toRegex()

    /**
     * Edge case
     * ```FLST 3469424155 /tmp/dri-state-Fri Aug 23 06:28:53 UTC 2024.txt 8216 Fri Aug 23 06:28:53 2024 9 1024 FLST```
     * It is impossible to find `8216` between file name and date without accepting date format as `Fri Aug 23 06:28:53 2024`
     */
    private val flstRegexp =
        """FLST\s(?<serial>\d+)\s(?<name>.*\.(\w|\d)+)\s(?<size>\d+)\s(?<creationdate>.*)\s(?<numpackages>\d+)\s(?<bufsize>\d+).*FLST""".toRegex()

    private val fldaRegexp =
        """FLDA\s(?<serial>\d+)\s(?<packagenum>\d+)\s(?<hexdata>.*)\sFLDA""".toRegex()

    @OptIn(ExperimentalStdlibApi::class)
    fun searchForFiles(payload: String) {
        if (payload.startsWith("FLIF")) {
            val matches = flifRegexp.find(payload)
            if (matches != null) {
                val serialNumber: Long = matches.groups["serial"]?.value?.toLong() ?: -1L
                val fileName: String = matches.groups["name"]?.value ?: ""
                val fileSize: Long = matches.groups["size"]?.value?.toLong() ?: -1
                val creationDate: String = matches.groups["creationdate"]?.value ?: ""
                val numberOfPackages: Int = matches.groups["numpackages"]?.value?.toInt() ?: -1

                val fileEntry = FileEntry()
                fileEntry.serialNumber = serialNumber
                fileEntry.name = fileName
                fileEntry.size = fileSize
                fileEntry.creationDate = creationDate
                fileEntry.numberOfPackages = numberOfPackages

//                filesMap[serialNumber] = fileEntry
            }
        } else if (payload.startsWith("FLST")) {
            val matches = flstRegexp.find(payload)
            if (matches != null) {
                val serialNumber: Long = matches.groups["serial"]?.value?.toLong() ?: -1L
                val fileName: String = matches.groups["name"]?.value ?: ""
                val fileSize: Long = matches.groups["size"]?.value?.toLong() ?: -1
                val creationDate: String = matches.groups["creationdate"]?.value ?: ""
                val numberOfPackages: Int = matches.groups["numpackages"]?.value?.toInt() ?: -1
                val bufferSize: Int = matches.groups["bufsize"]?.value?.toInt() ?: -1

                val fileEntry = FileEntry()
                fileEntry.serialNumber = serialNumber
                fileEntry.name = fileName
                fileEntry.size = fileSize
                fileEntry.creationDate = creationDate
                fileEntry.numberOfPackages = numberOfPackages
                fileEntry.bufferSize = bufferSize
                fileEntry.bytes = Array<ByteArray>(numberOfPackages, { i -> ByteArray(1) })

                filesMap[serialNumber] = fileEntry
            }

        } else if (payload.startsWith("FLDA")) {
            val matches = fldaRegexp.find(payload)
            if (matches != null) {
                val serialNumber: Long = matches.groups["serial"]?.value?.toLong() ?: -1L
                val packageNum: Int = matches.groups["packagenum"]?.value?.toInt() ?: -1
                val hexData: String? = matches.groups["hexdata"]?.value
                val fileEntry = filesMap[serialNumber]

                val bytes = fileEntry?.bytes
                if (fileEntry != null && hexData != null && packageNum >= 0 && bytes != null) {
                    bytes[packageNum - 1] = hexData.split(" ")
                        .map { it.toInt(16).toByte() }
                        .toByteArray()
                }
            }
        }
    }
}


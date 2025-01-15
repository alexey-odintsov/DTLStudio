package com.alekso.dltparser.dlt

import com.alekso.dltparser.dlt.extendedheader.ExtendedHeader
import com.alekso.dltparser.dlt.standardheader.StandardHeader

enum class PayloadStorageType {
    Structured,
    Plain,
    Binary,
}

/**
 * DLT message simplified representations.
 * https://github.com/esrlabs/dlt-core
 */
abstract class DLTMessage (
    val timeStampNano: Long,
    val standardHeader: StandardHeader,
    val extendedHeader: ExtendedHeader?,
) {
    /**
     * Payload textual representation - is used by search and timeline parsing.
     */
    abstract fun payloadText(): String?

    /**
     * Payload raw bytes - can be used to parse binary data
     */
    abstract fun payloadBytes(): ByteArray?

}

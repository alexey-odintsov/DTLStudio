package com.alekso.dltparser.dlt


/**
 * https://github.com/esrlabs/dlt-core
 */
data class DLTMessage(
    val timeStampSec: Int,
    val timeStampUs: Int,
    val ecuId: String,

    val standardHeader: StandardHeader,
    val extendedHeader: ExtendedHeader?,
    val payload: Payload?,
    // meta info
    val sizeBytes: Int,
) {

    fun getTimeStamp(): Long {
        return timeStampSec * 1000L + timeStampUs / 1000
    }

    override fun toString(): String {
        return "{$timeStampSec, $timeStampUs, '$ecuId'\n" +
                " $standardHeader\n" +
                " $extendedHeader}\n" +
                " $payload}\n" +
                " meta size bytes: $sizeBytes"
    }
}
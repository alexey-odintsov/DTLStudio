package com.alekso.dltparser.dlt


data class StandardHeader(
    /**
     * General info about message
     */
    val headerType: HeaderType,
    /**
     * Number of logs transmitted to selected channel
     */
    val messageCounter: UByte,
    /**
     * Overall length of DLT message: Standard header + Extended header + Payload
     */
    val length: UShort,
    /**
     * Which ECU has sent message
     */
    val ecuId: String?,
    /**
     * Source of a log/trace within ECU
     */
    val sessionId: Int?,
    /**
     * When message was generated
     */
    val timeStamp: UInt?,
) {

    fun getSize(): Int {
        var size = 4
        if (headerType.withEcuId) size += 4
        if (headerType.withSessionId) size += 4
        if (headerType.withTimestamp) size += 4
        return size
    }

    data class HeaderType(
        val originalByte: Byte,
        val useExtendedHeader: Boolean,
        val payloadBigEndian: Boolean,
        val withEcuId: Boolean,
        val withSessionId: Boolean,
        val withTimestamp: Boolean,
        val versionNumber: Byte // 3 bits
    ) {
    }
}
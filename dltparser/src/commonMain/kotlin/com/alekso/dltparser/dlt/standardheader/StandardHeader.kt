package com.alekso.dltparser.dlt.standardheader

import com.alekso.dltparser.Endian
import com.alekso.dltparser.isBitSet
import com.alekso.dltparser.readString
import com.alekso.dltparser.readUShort
import java.io.RandomAccessFile


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
    val ecuId: String? = null,
    /**
     * Source of a log/trace within ECU
     */
    val sessionId: Int? = null,
    /**
     * When message was generated
     */
    val timeStamp: UInt? = null,
) {

    fun getSize(): Int {
        var size = 4
        if (headerType.withEcuId) size += 4
        if (headerType.withSessionId) size += 4
        if (headerType.withTimestamp) size += 4
        return size
    }

    companion object {
        fun getSize(file: RandomAccessFile): Int {
            val headerType = file.readByte()
            val messageCounter = file.readUnsignedByte().toUByte()
            val length = file.readUnsignedShort().toUShort()
            val withEcuId = withEcuId(headerType)
            val withSessionId = withSessionId(headerType)
            val withTimestamp = withTimestamp(headerType)

            return 1 + 1 + 2
            +if (withEcuId) 4 else 0
            +if (withSessionId) 4 else 0
            +if (withTimestamp) 4 else 0
        }

        fun getHeaderSize(headerType: Byte): Int {
            return return (1 + 1 + 2
                    +if (withEcuId(headerType)) 4 else 0
                    +if (withSessionId(headerType)) 4 else 0
                    +if (withTimestamp(headerType)) 4 else 0)
        }

        fun useExtendedHeader(headerType: Byte): Boolean = headerType.isBitSet(0)
        fun payloadBigEndian(headerType: Byte): Boolean = headerType.isBitSet(1)
        fun withEcuId(headerType: Byte): Boolean = headerType.isBitSet(2)
        fun withSessionId(headerType: Byte): Boolean = headerType.isBitSet(3)
        fun withTimestamp(headerType: Byte): Boolean = headerType.isBitSet(4)
        fun versionNumber(headerType: Byte): Byte = (headerType.toInt() shr 5).toByte()

        fun length(standardHeader: ByteArray): Int  {
            return standardHeader.readUShort(2, Endian.BIG).toInt()
        }
        fun ecuId(standardHeader: ByteArray): String {
            return standardHeader.readString(4, 4)
        }

        fun sessionId(standardHeader: ByteArray): Int {
            return 0
        }

        fun timeStamp(standardHeader: ByteArray): UInt {
            return 0U
        }
    }
}
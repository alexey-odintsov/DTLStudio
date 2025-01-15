package com.alekso.dltparser.dlt.extendedheader

import com.alekso.dltparser.dlt.extendedheader.MessageInfo.Companion.messageTypeInfoFromByte
import com.alekso.dltparser.isBitSet

private const val EXTENDED_HEADER_SIZE = 10

data class ExtendedHeader(
    val messageInfo: MessageInfo,
    val argumentsCount: UByte,
    val applicationId: String,
    val contextId: String,
) {

    fun getSize(): Int = EXTENDED_HEADER_SIZE

    companion object {
        fun verbose(header: ByteArray): Boolean {
            return header[0].isBitSet(0)
        }

        fun messageType(header: ByteArray): MessageType {
            return messageTypeInfoFromByte(header[0])
        }

        fun messageTypeInfo(header: ByteArray): MessageTypeInfo {
            return messageTypeInfoFromByte(header[0], messageTypeInfoFromByte(header[0]))
        }

        fun contextId(header: ByteArray): String {
            if (header.size < 11) {
                return ""
            } else {
                return String(header, 7, 4)
            }
        }

        fun applicationId(header: ByteArray): String {
            return String(header, 3, 4)
        }
    }
}
package com.alekso.dltparser.dlt.verbosepayload

import com.alekso.dltparser.Endian
import com.alekso.dltparser.readInt
import com.alekso.dltparser.readShort
import com.alekso.dltparser.toHex

data class Argument(
    val typeInfoInt: Int,
    val typeInfo: TypeInfo,
    val additionalSize: Int,
    val payloadSize: Int,
    val payload: ByteArray,
) {
    fun getSize(): Int {
        return additionalSize + payload.size
    }

    fun getPayloadAsText(): String {
        val result = when {
            typeInfo.typeString -> String(payload)
            typeInfo.typeUnsigned -> {
                when (typeInfo.typeLengthBits) {
                    8 -> "${payload[0].toUInt()}"
                    16 -> "${payload.readShort(0, Endian.LITTLE).toUShort()}"
                    else -> try {
                        "${payload.readInt(0, Endian.LITTLE).toUInt()}"
                    } catch (e: Exception) {
                        e.toString()
                    }
                }
            }

            typeInfo.typeSigned ->
                when (typeInfo.typeLengthBits) {
                    8 -> "${payload[0].toInt()}"
                    16 -> "${payload.readShort(0, Endian.LITTLE).toInt()}"
                    else -> try {
                        "${payload.readInt(0, Endian.LITTLE).toUInt()}"
                    } catch (e: Exception) {
                        e.toString()
                    }
                }

            typeInfo.typeBool -> if (payload[0] == 0.toByte()) "FALSE" else "TRUE"
            else -> payload.toHex() // TODO: Add other types
        }
        return result.replace("\u0000", "").replace("\n", "")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Argument

        if (typeInfoInt != other.typeInfoInt) return false
        if (typeInfo != other.typeInfo) return false
        if (additionalSize != other.additionalSize) return false
        if (payloadSize != other.payloadSize) return false
        return payload.contentEquals(other.payload)
    }

    override fun hashCode(): Int {
        var result = typeInfoInt
        result = 31 * result + typeInfo.hashCode()
        result = 31 * result + additionalSize
        result = 31 * result + payloadSize
        result = 31 * result + payload.contentHashCode()
        return result
    }

}
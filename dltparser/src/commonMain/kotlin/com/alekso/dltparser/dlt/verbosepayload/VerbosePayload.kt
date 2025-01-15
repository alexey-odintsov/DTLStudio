package com.alekso.dltparser.dlt.verbosepayload

import com.alekso.dltparser.Endian
import com.alekso.dltparser.dlt.Payload
import com.alekso.dltparser.readInt
import com.alekso.dltparser.readUShort
import com.alekso.logger.Log


data class VerbosePayload(
    val arguments: List<Argument>
) : Payload {

    override fun getSize(): Int {
        var size = 0
        arguments.forEach { size += it.getSize() }
        return size
    }

    override fun asText(): String = arguments.joinToString(" ") { it.getPayloadAsText() }

    companion object {
        fun parse(
            payloadBytes: ByteArray,
            argumentsCount: Int,
            payloadEndian: Endian
        ): VerbosePayload {
            val arguments = mutableListOf<Argument>()

            var offset = 0
            try {
                repeat(argumentsCount) {
                    val verbosePayloadArgument: Argument = parseVerbosePayload(
                        offset,
                        payloadBytes,
                        payloadEndian
                    )
                    arguments.add(verbosePayloadArgument)
                    offset += verbosePayloadArgument.payloadSize + verbosePayloadArgument.additionalSize
                }
            } catch (e: Exception) {
                Log.e(e.toString())
            }

            return VerbosePayload(arguments)
        }

        private fun parseVerbosePayload(
            offset: Int, payloadBytes: ByteArray, payloadEndian: Endian
        ): Argument {
            val typeInfoInt = payloadBytes.readInt(offset, payloadEndian)
            val typeInfo = TypeInfo.parseVerbosePayloadTypeInfo(false, typeInfoInt, payloadEndian)

            var payloadSize: Int
            var additionalSize = 4
            if (typeInfo.typeString) {
                payloadSize = payloadBytes.readUShort(offset + 4, payloadEndian).toInt()
                // PRS_Dlt_00156 - 16-bit unsigned integer specifies the length of the string
                additionalSize += 2
            } else if (typeInfo.typeRaw) {
                payloadSize = payloadBytes.readUShort(offset + 4, payloadEndian).toInt()
                // PRS_Dlt_00160 - 16-bit unsigned integer shall specify the length of the raw data in byte
                additionalSize += 2
            } else if (typeInfo.typeUnsigned || typeInfo.typeSigned) {
                payloadSize = typeInfo.typeLengthBits / 8
            } else if (typeInfo.typeBool) {
                payloadSize = 1
            } else if (typeInfo.typeFloat) {
                payloadSize = typeInfo.typeLengthBits / 8
            } else {
                Log.e("Can't parse payload for typeInfo: $typeInfo")
                payloadSize = typeInfo.typeLengthBits / 8
            }

            // Sanity check to fix infinite reading
            if (payloadSize <= 0) {
                payloadSize = 1
            }

            val payloadOffset = offset + additionalSize
            val payload = payloadBytes.sliceArray(payloadOffset..payloadOffset + payloadSize - 1)

            val argument = Argument(
                typeInfoInt, typeInfo, additionalSize, payloadSize, payload
            )

            return argument
        }
    }
}
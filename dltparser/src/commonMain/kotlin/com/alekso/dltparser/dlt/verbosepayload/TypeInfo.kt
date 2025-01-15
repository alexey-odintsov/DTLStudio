package com.alekso.dltparser.dlt.verbosepayload

import com.alekso.dltparser.DLTParser.Companion.DEBUG_LOG
import com.alekso.dltparser.DLTParser.Companion.STRING_CODING_MASK
import com.alekso.dltparser.Endian
import com.alekso.dltparser.isBitSet
import com.alekso.dltparser.toBinary
import com.alekso.dltparser.toHex

data class TypeInfo(
    val typeLengthBits: Int = 0,
    val typeBool: Boolean = false,
    val typeSigned: Boolean = false,
    val typeUnsigned: Boolean = false,
    val typeFloat: Boolean = false,
    val typeArray: Boolean = false,
    val typeRaw: Boolean = false,
    val typeString: Boolean = false,
    val variableInfo: Boolean = false,
    val fixedPoint: Boolean = false,
    val traceInfo: Boolean = false,
    val typeStruct: Boolean = false,
    val stringCoding: StringCoding = StringCoding.ASCII,
) {
    enum class StringCoding {
        ASCII,
        UTF8,
        RESERVED
    }

    override fun toString(): String {
        val result = "len: $typeLengthBits bits (${typeLengthBits / 8} bytes); "
        return "$result; ${getTypeString()}; $stringCoding"
    }

    fun getTypeString(): String {
        val typeList = mutableListOf<String>()
        if (typeBool) {
            typeList.add("BOOL")
        }
        if (typeSigned) {
            typeList.add("SIGNED")
        }
        if (typeUnsigned) {
            typeList.add("UNSIGNED")
        }
        if (typeFloat) {
            typeList.add("FLOAT")
        }
        if (typeArray) {
            typeList.add("ARRAY")
        }
        if (typeRaw) {
            typeList.add("RAW")
        }
        if (typeString) {
            typeList.add("STRING")
        }
        if (variableInfo) {
            typeList.add("VARIABLE_INFO")
        }
        if (fixedPoint) {
            typeList.add("FIXED_POINT")
        }
        if (traceInfo) {
            typeList.add("TRACE_INFO")
        }
        if (typeStruct) {
            typeList.add("STRUCT")
        }
        return typeList.toString()
    }

    companion object {
        fun parseVerbosePayloadTypeInfo(
            shouldLog: Boolean, typeInfoInt: Int, payloadEndian: Endian
        ): TypeInfo {
            val typeLengthBits = when (typeInfoInt and 0b1111) {
                0b0001 -> 8
                0b0010 -> 16
                0b0011 -> 32
                0b0100 -> 64
                0b0101 -> 128
                else -> 0
            }

            val stringCoding = when (typeInfoInt.shr(15) and STRING_CODING_MASK) {
                0 -> StringCoding.ASCII
                1 -> StringCoding.UTF8
                else -> StringCoding.RESERVED
            }

            if (DEBUG_LOG && shouldLog) {
                println("   typeInfoInt: 0x${typeInfoInt.toHex(4)} (${typeInfoInt.toBinary(32)}b)")
                println("   typeLengthBits: $typeLengthBits (${(typeInfoInt and 0b1111).toBinary(32)}b)")
                println(
                    "   stringCoding: $stringCoding (${
                        (typeInfoInt.shr(15) and STRING_CODING_MASK).toBinary(32)
                    }b)"
                )
            }

            return TypeInfo(
                typeLengthBits,
                typeBool = typeInfoInt.isBitSet(4),
                typeSigned = typeInfoInt.isBitSet(5),
                typeUnsigned = typeInfoInt.isBitSet(6),
                typeFloat = typeInfoInt.isBitSet(7),
                typeArray = typeInfoInt.isBitSet(8),
                typeString = typeInfoInt.isBitSet(9),
                typeRaw = typeInfoInt.isBitSet(10),
                variableInfo = typeInfoInt.isBitSet(11),
                fixedPoint = typeInfoInt.isBitSet(12),
                traceInfo = typeInfoInt.isBitSet(13),
                typeStruct = typeInfoInt.isBitSet(14),
                stringCoding = stringCoding
            )
        }
    }
}
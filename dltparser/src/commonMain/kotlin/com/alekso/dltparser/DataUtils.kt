package com.alekso.dltparser


private const val DEBUG_LOG = false

enum class Endian {
    BIG, LITTLE
}

// region isBitSet
fun Byte.isBitSet(position: Int): Boolean {
    val mask = 1 shl position
    val result = this.toInt() and mask
    if (DEBUG_LOG) {
        println("this:   " + toBinary(8))
        println("mask:   " + mask.toBinary(8))
        println("masked: " + result.toBinary(8))
    }
    return (this.toInt() and result) == mask
}

fun Short.isBitSet(position: Int): Boolean {
    val mask = 1 shl position
    val result = this.toInt() and mask
    if (DEBUG_LOG) {
        println("this:   " + toBinary(16))
        println("mask:   " + mask.toBinary(16))
        println("masked: " + result.toBinary(16))
    }
    return (this.toInt() and result) == mask
}

fun Int.isBitSet(position: Int): Boolean {
    val mask = 1 shl position
    val result = this and mask
    if (DEBUG_LOG) {
        println("this:   " + toBinary(32))
        println("mask:   " + mask.toBinary(32))
        println("masked: " + result.toBinary(32))
    }
    return (this and result) == mask
}

/**
 * Checks if bit at position is set (LSB order)
 */
fun Long.isBitSet(position: Int): Boolean {
    val mask = 1L shl position
    val result = this and mask
    if (DEBUG_LOG) {
        println("this:   " + toBinary(64))
        println("mask:   " + mask.toBinary(64))
        println("masked: " + result.toBinary(64))
    }
    return (this and result) == mask
}
// endregion

// region toBinary
fun Int.toBinary(bits: Int): String {
    return toUInt().toString(2).padStart(bits, '0')
}

fun Byte.toBinary(bits: Int): String {
    return toUByte().toString(2).padStart(bits, '0')
}

fun Short.toBinary(bits: Int): String {
    return toUShort().toString(2).padStart(bits, '0')
}

fun Long.toBinary(bits: Int): String {
    return toULong().toString(2).padStart(bits, '0')
}
// endregion

// region toHex
fun Int.toHex(bytes: Int): String {
    return toUInt().toString(16).padStart(bytes * 2, '0')
}

fun Short.toHex(bytes: Int): String {
    return toUShort().toString(16).padStart(bytes * 2, '0')
}

fun Byte.toHex(): String = "%02x".format(this)

fun Long.toHex(bytes: Int): String {
    return toULong().toString(16).padStart(bytes * 2, '0')
}


fun ByteArray.toHex(): String =
    joinToString(separator = " ") { eachByte -> "%02x".format(eachByte) }
// endregion


// region ByteArray reading

fun ByteArray.readString(start: Int, bytes: Int) = decodeToString(start, start + bytes)

/**
 * Reads 2 bytes data signed Short
 */
fun ByteArray.readShort(start: Int, endian: Endian): Short {
    if (size < 2) {
        throw IllegalStateException("Trying to read 2 bytes from $size byteArray")
    }

    return if (endian == Endian.LITTLE) {
        ((get(start + 1).toInt() shl 8) or (get(start + 0).toInt())).toShort()
    } else {
        ((get(start + 0).toInt() shl 8) or (get(start + 1).toInt())).toShort()
    }
}

/**
 * Reads 2 bytes data signed Short
 */
fun ByteArray.readUShort(start: Int, endian: Endian): UShort {
    if (size < 2) {
        throw IllegalStateException("Trying to read 2 bytes from $size byteArray")
    }

    return if (endian == Endian.LITTLE) {
        (((get(start + 1).toInt() and 255) shl 8) or (get(start).toInt() and 255)).toUShort()
    } else {
        (((get(start).toInt() and 255) shl 8) or (get(start + 1).toInt() and 255)).toUShort()
    }
}

/**
 * Reads 4 bytes signed Int
 */
fun ByteArray.readInt(start: Int, endian: Endian): Int {
    if (size < 4) {
        throw IllegalStateException("Trying to read 4 bytes from $size byteArray")
    }

    return if (endian == Endian.LITTLE) {
        (get(start + 3).toInt() shl 24) or (get(start + 2).toInt() and 0xff shl 16) or (get(start + 1).toInt() and 0xff shl 8) or (get(
            start + 0
        ).toInt() and 0xff)
    } else {
        (get(start + 0).toInt() shl 24) or (get(start + 1).toInt() and 0xff shl 16) or (get(start + 2).toInt() and 0xff shl 8) or (get(
            start + 3
        ).toInt() and 0xff)
    }
}
// endregion
package com.alekso.dltparser.tests.parserv1

import com.alekso.dltparser.dlt.PayloadStorageType
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import com.alekso.dltparser.tests.testdata.TestData

class DLTParserV1Tests {
    val parser = DLTParserV1(PayloadStorageType.Structured)

    @Test
    fun `DLTParserV1 parse DLT_MESSAGE_1`() {
        val data = TestData.DLT_MESSAGE_1.map { it.toByte() }.toByteArray()
        val actual = parser.parseDLTMessage(data, 0, true)
        val expected = TestData.DLT_MESSAGE_1_PARSED
        Assert.assertTrue("actual: $actual\nexpected: $expected", actual == expected)
    }

    @Test
    fun `DLTParserV1 parse DLT_MESSAGE_2`() {
        val data = TestData.DLT_MESSAGE_2.map { it.toByte() }.toByteArray()
        val actual = parser.parseDLTMessage(data, 0, true)
        val expected = TestData.DLT_MESSAGE_2_PARSED
        Assert.assertTrue("actual: $actual\nexpected: $expected", actual == expected)
    }

    @Ignore
    @Test
    fun `DLTParserV1 parse DLT_MESSAGE_BROKEN_1`() {
        val data = TestData.DLT_MESSAGE_BROKEN_1.map { it.toByte() }.toByteArray()
        val actual = parser.parseDLTMessage(data, 0, true)
        val expected = TestData.DLT_MESSAGE_BROKEN_1_PARSED
        Assert.assertTrue("actual: $actual\nexpected: $expected", actual == expected)
    }
}
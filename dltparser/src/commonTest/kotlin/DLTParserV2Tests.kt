import com.alekso.dltparser.DLTParserV2
import com.alekso.dltparser.ParserInputStream
import org.junit.Assert
import org.junit.Test
import testdata.TestData

class DLTParserV2Tests {

    @Test
    fun `DTLParserV2 parse DLT_MESSAGE_1`() {
        val data = TestData.DLT_MESSAGE_1.map { it.toByte() }.toByteArray().inputStream()
        val pis = ParserInputStream(data).also { it.skipNBytes(4) }
        val actual = DLTParserV2().parseDLTMessage(pis, 0, true)
        val expected = TestData.DLT_MESSAGE_1_PARSED
        Assert.assertTrue("actual: $actual\nexpected: $expected", actual == expected)
    }

    @Test
    fun `DTLParserV2 parse DLT_MESSAGE_2`() {
        val data = TestData.DLT_MESSAGE_2.map { it.toByte() }.toByteArray().inputStream()
        val pis = ParserInputStream(data).also { it.skipNBytes(4) }
        val actual = DLTParserV2().parseDLTMessage(pis, 0, true)
        val expected = TestData.DLT_MESSAGE_2_PARSED
        Assert.assertTrue("actual: $actual\nexpected: $expected", actual == expected)
    }

    @Test
    fun `DTLParserV2 parse DLT_MESSAGE_BROKEN_1`() {
        val data = TestData.DLT_MESSAGE_BROKEN_1.map { it.toByte() }.toByteArray().inputStream()
        val pis = ParserInputStream(data).also { it.skipNBytes(4) }
        val actual = DLTParserV2().parseDLTMessage(pis, 0, true)
        val expected = TestData.DLT_MESSAGE_BROKEN_1_PARSED
        Assert.assertTrue("actual: $actual\nexpected: $expected", actual == expected)
    }
}
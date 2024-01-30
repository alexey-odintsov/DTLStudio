import com.alekso.dltparser.DLTParser
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.ExtendedHeader
import com.alekso.dltparser.dlt.MessageInfo
import com.alekso.dltparser.dlt.StandardHeader
import com.alekso.dltparser.dlt.VerbosePayload
import org.junit.Assert
import org.junit.Test

class DLTMessageParsingTest {
    val dltMessage = DLTMessage(
        21142234, 243243, "MGUA",
        StandardHeader(
            StandardHeader.HeaderType(0.toByte(), true, true, true, true, true, 1),
            10.toUByte(), 10U, "MGUA", 106, 332422U
        ),
        ExtendedHeader(
            MessageInfo(
                30.toByte(),
                true,
                MessageInfo.MESSAGE_TYPE.DLT_TYPE_LOG,
                MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_INFO
            ), 1U, "SGFX", "COMP"
        ),
        VerbosePayload(
            listOf(
                VerbosePayload.Argument(
                    1,
                    VerbosePayload.TypeInfo(
                        typeString = true,
                        stringCoding = VerbosePayload.TypeInfo.STRING_CODING.UTF8
                    ),
                    12,
                    63,
                    "Page flip enqueued on connector 260 with handler 0x73d8005740".toByteArray()
                )
            )
        ),
        122
    )

    @Test
    fun parseDLTMessage1() {
        // @formatter:off
        val raw = listOf(
            0x44, 0x4C, 0x54, 0x01, 0x06, 0x8D, 0x85, 0x65, 0x8B, 0xD5, 0x04, 0x00, 0x4D, 0x47, 0x55,
            0x41 ,0x3D, 0x26, 0x00, 0x5F, 0x4D, 0x47, 0x55, 0x41, 0x00, 0x00, 0x00, 0x6A, 0x01, 0xEE,
            0xBA, 0x12, 0x41, 0x01, 0x53, 0x47, 0x46, 0x58, 0x43, 0x4F, 0x4D, 0x50, 0x00, 0x02, 0x00,
            0x00, 0x3F, 0x00, 0x50, 0x61, 0x67, 0x65, 0x20, 0x66, 0x6C, 0x69, 0x70, 0x20, 0x65, 0x6E,
            0x71, 0x75, 0x65, 0x75, 0x65, 0x64, 0x20, 0x6F, 0x6E, 0x20, 0x63, 0x6F, 0x6E, 0x6E, 0x65,
            0x63, 0x74, 0x6F, 0x72, 0x20, 0x32, 0x36, 0x30, 0x20, 0x77, 0x69, 0x74, 0x68, 0x20, 0x68,
            0x61, 0x6E, 0x64, 0x6C, 0x65, 0x72, 0x20, 0x30, 0x78, 0x37, 0x33, 0x64, 0x38, 0x30, 0x30,
            0x35, 0x37, 0x34, 0x30, 0x0A, 0x00,
        )
        // @formatter:on
        val data = raw.map { it.toByte() }.toByteArray()
        val actual = DLTParser.parseDLTMessage(data, 0, true)
        val expected = dltMessage
        Assert.assertTrue("$actual != $expected", actual == expected)
    }

    @Test
    fun parseDLTMessage2() {
        // @formatter:off
        val raw = listOf(
        0x44, 0x4c, 0x54, 0x01, 0x06, 0x8d, 0x85, 0x65, 0xb9, 0xdd, 0x04, 0x00, 0x4d, 0x47, 0x55, 0x41,
        0x3d, 0xcc, 0x00, 0xc4, 0x4d, 0x47, 0x55, 0x41, 0x00, 0x00, 0x00, 0x2b, 0x01, 0xee, 0xba, 0x29,
        0x41, 0x01, 0x41, 0x52, 0x43, 0x00, 0x41, 0x52, 0x43, 0x00,
        0x00, 0x02, 0x00, 0x00, 0xa4, 0x00, 0x4f, 0x6e, 0x6c, 0x69, 0x6e, 0x65, 0x43, 0x61, 0x6c, 0x69,
        0x62, 0x72, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x2e, 0x63, 0x70, 0x70, 0x20, 0x6f, 0x6e, 0x4c, 0x6f,
        0x67, 0x3a, 0x31, 0x31, 0x34, 0x20, 0x5b, 0x46, 0x52, 0x41, 0x4d, 0x45, 0x2d, 0x49, 0x4e, 0x46,
        0x4f, 0x5d, 0x20, 0x53, 0x69, 0x67, 0x6e, 0x61, 0x6c, 0x73, 0x20, 0x6e, 0x6f, 0x74, 0x20, 0x69,
        0x6e, 0x20, 0x74, 0x68, 0x72, 0x65, 0x73, 0x68, 0x6f, 0x6c, 0x64, 0x2c, 0x20, 0x54, 0x69, 0x6d,
        0x65, 0x73, 0x74, 0x61, 0x6d, 0x70, 0x3a, 0x20, 0x33, 0x31, 0x30, 0x33, 0x31, 0x38, 0x34, 0x2c,
        0x20, 0x50, 0x69, 0x74, 0x63, 0x68, 0x52, 0x61, 0x74, 0x65, 0x3a, 0x20, 0x30, 0x2c, 0x20, 0x59,
        0x61, 0x77, 0x52, 0x61, 0x74, 0x65, 0x3a, 0x20, 0x34, 0x2e, 0x39, 0x38, 0x35, 0x2c, 0x20, 0x52,
        0x6f, 0x6c, 0x6c, 0x52, 0x61, 0x74, 0x65, 0x3a, 0x20, 0x30, 0x2c, 0x20, 0x53, 0x70, 0x65, 0x65,
        0x64, 0x3a, 0x20, 0x32, 0x32, 0x2e, 0x37, 0x39, 0x36, 0x39, 0x2c, 0x20, 0x4d, 0x69, 0x6c, 0x65,
        0x61, 0x67, 0x65, 0x3a, 0x20, 0x4e, 0x2f, 0x41, 0x0a, 0x00 )
        // @formatter:on

        val data = raw.map { it.toByte() }.toByteArray()
        val actual = DLTParser.parseDLTMessage(data, 0, false)
        val expected = actual
        Assert.assertTrue("$actual != $expected", actual == expected)
    }
}
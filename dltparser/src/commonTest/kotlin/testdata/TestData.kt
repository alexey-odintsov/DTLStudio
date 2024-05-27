package testdata

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.extendedheader.ExtendedHeader
import com.alekso.dltparser.dlt.extendedheader.MessageInfo
import com.alekso.dltparser.dlt.extendedheader.MessageType
import com.alekso.dltparser.dlt.extendedheader.MessageTypeInfo
import com.alekso.dltparser.dlt.standardheader.HeaderType
import com.alekso.dltparser.dlt.standardheader.StandardHeader
import com.alekso.dltparser.dlt.verbosepayload.Argument
import com.alekso.dltparser.dlt.verbosepayload.TypeInfo
import com.alekso.dltparser.dlt.verbosepayload.VerbosePayload

object TestData {

    // @formatter:off
    val DLT_MESSAGE_1 = listOf(
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
    internal val DLT_MESSAGE_1_PARSED = DLTMessage(
        1703251206316811, "MGUA",
        StandardHeader(
            HeaderType(
                61.toByte(),
                useExtendedHeader = true,
                payloadBigEndian = false,
                withEcuId = true,
                withSessionId = true,
                withTimestamp = true,
                versionNumber = 1
            ),
            38.toUByte(), 95U, "MGUA", 106, 32422418U
        ),
        ExtendedHeader(
            MessageInfo(
                65.toByte(),
                true,
                MessageType.DLT_TYPE_LOG,
                MessageTypeInfo.DLT_LOG_INFO
            ), 1U, "SGFX", "COMP"
        ),
        VerbosePayload(
            listOf(
                Argument(
                    512,
                    TypeInfo(
                        typeString = true,
                        stringCoding = TypeInfo.StringCoding.ASCII
                    ),
                    2,
                    63,
                    "Page flip enqueued on connector 260 with handler 0x73d8005740".toByteArray()
                )
            )
        ).asText(),
        111
    )

    // @formatter:off
    val DLT_MESSAGE_2 = listOf(
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

    internal val DLT_MESSAGE_2_PARSED = DLTMessage(
        1703251206318905, "MGUA",
        StandardHeader(
            HeaderType(
                61.toByte(),
                useExtendedHeader = true,
                payloadBigEndian = false,
                withEcuId = true,
                withSessionId = true,
                withTimestamp = true,
                versionNumber = 1
            ),
            204.toUByte(), 196U, "MGUA", 43, 32422441U
        ),
        ExtendedHeader(
            MessageInfo(
                65.toByte(),
                true,
                MessageType.DLT_TYPE_LOG,
                MessageTypeInfo.DLT_LOG_INFO
            ), 1U, "ARC", "ARC"
        ),
        VerbosePayload(
            listOf(
                Argument(
                    512,
                    TypeInfo(
                        typeString = true,
                        stringCoding = TypeInfo.StringCoding.ASCII
                    ),
                    2,
                    164,
                    "OnlineCalibration.cpp onLog:114 [FRAME-INFO] Signals not in threshold, Timestamp: 3103184, PitchRate: 0, YawRate: 4.985, RollRate: 0, Speed: 22.7969, Mileage: N/A".toByteArray()
                )
            )
        ).asText(),
        212
    )

    // @formatter:off
    val DLT_MESSAGE_BROKEN_1 = listOf(
        0x44, 0x4C, 0x54, 0x01, // DLT
        0x25, 0x83, 0xB6, 0x65, // TS Sec
        0x42, 0xFC, 0x09, 0x00, // TS Nano
        0x55, 0x48, 0x01, 0x01, // ECUID 'UH..'
        // Standard header
        0x3D, // HTYP
        0x19, // MCNT
        0x00, 0x44, // LEN (68)
        0x55, 0x48, 0x01, 0x01, // ECU ID 'UH..'
        0x00, 0x00, 0x00, 0x3D, // Session ID
        0x0B, 0x00, 0x41, 0x45, // Timestamp
        // Extended header
        0x43, // MSIN
        // U
        0x55, // NOAR
        // 1
        0x31, 0x00, 0x00, 0x5E, // App ID
        0xDD, 0x01, 0xBC, 0x84, // Context ID
        //    !           C     H     D     D     C     D     0
        0x82, 0x21, 0x01, 0x43, 0x48, 0x44, 0x44, 0x43, 0x44, 0x48, 0x00, 0x00, 0x02, 0x00, 0x00,
        //          F     i     l     e     ' '   s     y     s     t     e     m     ' '   p
        0x21, 0x00, 0x46, 0x69, 0x6C, 0x65, 0x20, 0x73, 0x79, 0x73, 0x74, 0x65, 0x6D, 0x20, 0x70,
        // a  t     h     ' '   l     e     n     g     h     t     ' '   t
        0x61, 0x74, 0x68, 0x20, 0x6C, 0x65, 0x6E, 0x67, 0x68, 0x74, 0x20, 0x74 )
    // @formatter:on


    internal val DLT_MESSAGE_BROKEN_1_PARSED = DLTMessage(
        1706459941654402, "UH\u0001\u0001",
        StandardHeader(
            HeaderType(
                61.toByte(),
                useExtendedHeader = true,
                payloadBigEndian = false,
                withEcuId = true,
                withSessionId = true,
                withTimestamp = true,
                versionNumber = 1
            ),
            25.toUByte(), 68U, "UH\u0001\u0001", 61, 184566085U
        ),
        ExtendedHeader(
            MessageInfo(
                67.toByte(),
                true,
                MessageType.DLT_TYPE_APP_TRACE,
                MessageTypeInfo.DLT_TRACE_STATE
            ), 85U, "SGFX", "COMP"
        ),
        VerbosePayload(
            listOf(
                Argument(
                    512,
                    TypeInfo(
                        typeString = true,
                        stringCoding = TypeInfo.StringCoding.ASCII
                    ),
                    2,
                    63,
                    "Page flip enqueued on connector 260 with handler 0x73d8005740".toByteArray()
                )
            )
        ).asText(),
        84
    )
}
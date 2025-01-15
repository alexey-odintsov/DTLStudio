package com.alekso.dltstudio.logs.infopanel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltparser.dlt.extendedheader.ExtendedHeader
import com.alekso.dltparser.toBinary
import com.alekso.dltparser.toHex
import com.alekso.dltstudio.model.LogMessage

@Composable
fun DLTDetailedInfoView(
    modifier: Modifier = Modifier,
    logMessage: LogMessage?,
    messageIndex: Int
) {
    val paddingModifier = Modifier.padding(start = 4.dp, end = 4.dp)

    SelectionContainer {
        Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            logMessage?.dltMessage?.let {
                val parameterRowWidth = 150
                Header(
                    modifier = paddingModifier,
                    text = "DLT Message #$messageIndex:"
                )
                TableRow(
                    parameterRowWidth, "Timestamp",
                    "${it.timeStampNano}"
                )
                TableRow(
                    parameterRowWidth, "ECU Id",
                    "'${it.standardHeader.ecuId}'"
                )

                Header(
                    modifier = paddingModifier,
                    text = "Standard header:"
                )

                TableRow(
                    parameterRowWidth, "Header Type",
                    "0x${it.standardHeader.headerType.originalByte.toHex()} " +
                            "(${it.standardHeader.headerType.originalByte.toBinary(8)}b)"
                )
                TableRow(
                    parameterRowWidth, "  Extender header",
                    "${it.standardHeader.headerType.useExtendedHeader}"
                )
                TableRow(
                    parameterRowWidth, "  Payload Endian",
                    if (it.standardHeader.headerType.payloadBigEndian) "BIG" else "LITTLE"
                )
                TableRow(
                    parameterRowWidth, "  ECU present",
                    "${it.standardHeader.headerType.withEcuId}"
                )
                TableRow(
                    parameterRowWidth, "  Session present",
                    "${it.standardHeader.headerType.withSessionId}"
                )
                TableRow(
                    parameterRowWidth, "  Timestamp present",
                    "${it.standardHeader.headerType.withTimestamp}"
                )
                TableRow(
                    parameterRowWidth, "  Version number",
                    "0x${it.standardHeader.headerType.versionNumber.toHex()}"
                )

                TableRow(
                    parameterRowWidth, "Message counter",
                    "${it.standardHeader.messageCounter}"
                )
                TableRow(
                    parameterRowWidth, "Length",
                    "${it.standardHeader.length}"
                )
                if (it.standardHeader.headerType.withEcuId) {
                    TableRow(
                        parameterRowWidth, "ECU Id",
                        "'${it.standardHeader.ecuId}'"
                    )
                }
                if (it.standardHeader.headerType.withSessionId) {
                    TableRow(
                        parameterRowWidth, "Session Id",
                        "${it.standardHeader.sessionId}"
                    )
                }

                if (it.standardHeader.headerType.withTimestamp) {
                    TableRow(
                        parameterRowWidth, "Timestamp",
                        "${it.standardHeader.timeStamp}"
                    )
                }

                if (it.extendedHeader != null) {
                    val extendedHeader: ExtendedHeader = it.extendedHeader!!
                    Header(
                        modifier = paddingModifier,
                        text = "Extended header:"
                    )
                    TableRow(
                        parameterRowWidth, "  Message info",
                        "0x${extendedHeader.messageInfo.originalByte.toHex()} " +
                                "(${extendedHeader.messageInfo.originalByte.toBinary(8)}b)"
                    )
                    TableRow(
                        parameterRowWidth, "  Verbose",
                        "${extendedHeader.messageInfo.verbose}"
                    )
                    TableRow(
                        parameterRowWidth, "  Message type",
                        "${extendedHeader.messageInfo.messageType}"
                    )
                    TableRow(
                        parameterRowWidth, "  Message type info",
                        "${extendedHeader.messageInfo.messageTypeInfo}"
                    )
                    TableRow(
                        parameterRowWidth, "Arguments count",
                        "${extendedHeader.argumentsCount}"
                    )
                    TableRow(
                        parameterRowWidth, "Application Id",
                        "'${extendedHeader.applicationId}'"
                    )
                    TableRow(
                        parameterRowWidth, "Context Id",
                        "'${extendedHeader.contextId}'"
                    )

                }

                Header(
                    modifier = paddingModifier,
                    text = "Payload:"
                )

                TableRow(0, "", it.payloadText())
            }
        }
    }
}
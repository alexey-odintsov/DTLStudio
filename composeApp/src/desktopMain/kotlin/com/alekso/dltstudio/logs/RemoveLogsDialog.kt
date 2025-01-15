package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.alekso.dltparser.dlt.extendedheader.MessageType
import com.alekso.dltparser.dlt.extendedheader.MessageTypeInfo
import com.alekso.dltstudio.logs.filtering.FilterCriteria
import com.alekso.dltstudio.logs.filtering.FilterParameter
import com.alekso.dltstudio.logs.filtering.TextCriteria
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.ui.CustomButton
import com.alekso.dltstudio.ui.CustomDropDown
import com.alekso.dltstudio.ui.CustomEditText
import com.alekso.dltstudio.utils.SampleData


class RemoveLogsDialogState(
    var visible: Boolean,
    val message: LogMessage? = null,
)

@Composable
fun RemoveLogsDialog(
    visible: Boolean,
    message: LogMessage? = null,
    onFilterClicked: (Map<FilterParameter, FilterCriteria>) -> Unit,
    onDialogClosed: () -> Unit,
) {
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = "Removing logs",
        state = rememberDialogState(width = 400.dp, height = 320.dp)
    ) {
        RemoveLogsDialogPanel(message, onFilterClicked, onDialogClosed)
    }
}

private val COL_NAME_SIZE_DP = 150.dp
private val SEARCH_INPUT_SIZE_DP = 250.dp
private val FILTER_TYPE = 150.dp

@Composable
fun RemoveLogsDialogPanel(
    message: LogMessage? = null,
    onFilterClicked: (Map<FilterParameter, FilterCriteria>) -> Unit,
    onDialogClosed: () -> Unit
) {

    val filters = mapOf<FilterParameter, FilterCriteria>(
        FilterParameter.EcuId to FilterCriteria(message?.dltMessage?.standardHeader?.ecuId ?: ""),
        FilterParameter.SessionId to FilterCriteria(
            message?.dltMessage?.standardHeader?.sessionId.toString()
        ),
        FilterParameter.ContextId to FilterCriteria(
            message?.dltMessage?.extendedHeader?.contextId.toString()
        ),
        FilterParameter.AppId to FilterCriteria(
            message?.dltMessage?.extendedHeader?.applicationId.toString()
        ),
        FilterParameter.MessageType to FilterCriteria(
            message?.dltMessage?.extendedHeader?.messageInfo?.messageType.toString()
        ),
        FilterParameter.MessageTypeInfo to FilterCriteria(
            message?.dltMessage?.extendedHeader?.messageInfo?.messageTypeInfo.toString()
        ),
        FilterParameter.Payload to FilterCriteria(message?.dltMessage?.payloadText() ?: ""),
    )

    var messageType by rememberSaveable { mutableStateOf(filters[FilterParameter.MessageType]?.value) }
    var messageTypeInfo by rememberSaveable { mutableStateOf(filters[FilterParameter.MessageTypeInfo]?.value) }
    var ecuId by rememberSaveable { mutableStateOf(filters[FilterParameter.EcuId]?.value) }
    var appId by rememberSaveable { mutableStateOf(filters[FilterParameter.AppId]?.value) }
    var contextId by rememberSaveable { mutableStateOf(filters[FilterParameter.ContextId]?.value) }
    var sessionId by rememberSaveable { mutableStateOf(filters[FilterParameter.SessionId]?.value) }
    var payload by rememberSaveable { mutableStateOf(filters[FilterParameter.Payload]?.value) }
    var payloadCriteria by rememberSaveable { mutableStateOf(filters[FilterParameter.Payload]?.textCriteria) }

    val colNameStyle = Modifier.width(COL_NAME_SIZE_DP).padding(horizontal = 4.dp)

    Column(
        Modifier.width(1000.dp).padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Row {
            val items = mutableListOf("Any")
            items.addAll(MessageType.entries.map { it.name })
            var initialSelection =
                items.indexOfFirst { it == filters[FilterParameter.MessageType]?.value }
            if (initialSelection == -1) initialSelection = 0

            Text(modifier = colNameStyle, text = "Message Type")
            CustomDropDown(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP).padding(horizontal = 4.dp),
                items = items,
                initialSelectedIndex = initialSelection,
                onItemsSelected = { i ->
                    messageType = if (i > 0) {
                        items[i]
                    } else null
                }
            )
        }

        Row {
            val items = mutableListOf("Any")
            items.addAll(MessageTypeInfo.entries.map { it.name })
            var initialSelection =
                items.indexOfFirst { it == filters[FilterParameter.MessageTypeInfo]?.value }
            if (initialSelection == -1) initialSelection = 0

            Text(modifier = colNameStyle, text = "Message Type Info")
            CustomDropDown(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP).padding(horizontal = 4.dp),
                items = items,
                initialSelectedIndex = initialSelection,
                onItemsSelected = { i ->
                    messageTypeInfo = if (i > 0) {
                        items[i]
                    } else null
                }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = colNameStyle, text = "ECU ID")
            CustomEditText(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP),
                value = ecuId ?: "", onValueChange = {
                    ecuId = it
                }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = colNameStyle, text = "App ID")
            CustomEditText(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP),
                value = appId ?: "", onValueChange = {
                    appId = it
                }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = colNameStyle, text = "Context ID")
            CustomEditText(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP),
                value = contextId ?: "", onValueChange = {
                    contextId = it
                }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = colNameStyle, text = "Session ID")
            CustomEditText(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP),
                value = sessionId ?: "", onValueChange = {
                    sessionId = it
                }
            )
        }

        val items = mutableListOf<String>()
        items.addAll(TextCriteria.entries.map { it.name })
        var initialSelection = items.indexOfFirst { it == payloadCriteria?.name }
        if (initialSelection == -1) initialSelection = 0

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = colNameStyle, text = "Payload")
            CustomDropDown(
                modifier = Modifier.width(FILTER_TYPE).padding(horizontal = 4.dp),
                items = items,
                initialSelectedIndex = initialSelection,
                onItemsSelected = { i ->
                    payloadCriteria = if (i > 0) {
                        TextCriteria.valueOf(items[i])
                    } else null
                }
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            CustomEditText(
                modifier = Modifier.fillMaxWidth().height(66.dp).align(Alignment.Top),
                singleLine = false,
                value = payload ?: "", onValueChange = {
                    payload = it
                }
            )
        }

        CustomButton(onClick = {
            val map = mutableMapOf<FilterParameter, FilterCriteria>()
            messageType?.let {
                map[FilterParameter.MessageType] = FilterCriteria(it, TextCriteria.PlainText)
            }
            messageTypeInfo?.let {
                map[FilterParameter.MessageTypeInfo] = FilterCriteria(it, TextCriteria.PlainText)
            }
            ecuId?.let {
                map[FilterParameter.EcuId] = FilterCriteria(it, TextCriteria.PlainText)
            }
            appId?.let {
                map[FilterParameter.AppId] = FilterCriteria(it, TextCriteria.PlainText)
            }
            contextId?.let {
                map[FilterParameter.ContextId] = FilterCriteria(it, TextCriteria.PlainText)
            }
            sessionId?.let {
                map[FilterParameter.SessionId] = FilterCriteria(it, TextCriteria.PlainText)
            }
            payload?.let {
                map[FilterParameter.Payload] =
                    FilterCriteria(it, payloadCriteria ?: TextCriteria.PlainText)
            }
            onFilterClicked(map)
            onDialogClosed()
        }) {
            Text(text = "Remove logs")
        }

    }
}

@Preview
@Composable
fun PreviewRemoveLogsDialogPanel() {
    val message = LogMessage(SampleData.getSampleDltMessages(1)[0])

    Column(Modifier.width(550.dp).background(Color(238, 238, 238))) {
        RemoveLogsDialogPanel(message, { f -> }) {}
    }
}
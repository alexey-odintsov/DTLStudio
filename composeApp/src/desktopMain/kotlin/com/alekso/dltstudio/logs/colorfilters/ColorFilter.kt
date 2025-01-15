package com.alekso.dltstudio.logs.colorfilters

import androidx.compose.ui.graphics.Color
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.extendedheader.MessageTypeInfo
import com.alekso.dltstudio.logs.CellStyle
import com.alekso.dltstudio.logs.filtering.FilterCriteria
import com.alekso.dltstudio.logs.filtering.FilterParameter
import com.alekso.dltstudio.logs.filtering.TextCriteria
import com.alekso.dltstudio.logs.filtering.checkTextCriteria
import kotlinx.serialization.Serializable


@Serializable
data class ColorFilter(
    val name: String,
    val filters: Map<FilterParameter, FilterCriteria>,
    val cellStyle: CellStyle,
    val enabled: Boolean = true,
) {
    fun assess(message: DLTMessage): Boolean {
        return filters.all {
            val criteria = it.value
            return@all enabled && when (it.key) {
                FilterParameter.MessageType -> {
                    checkTextCriteria(
                        criteria,
                        message.extendedHeader?.messageInfo?.messageType?.name
                    )
                }

                FilterParameter.MessageTypeInfo -> {
                    checkTextCriteria(
                        criteria,
                        message.extendedHeader?.messageInfo?.messageTypeInfo?.name
                    )
                }

                FilterParameter.EcuId -> {
                    checkTextCriteria(criteria, message.standardHeader.ecuId)
                }

                FilterParameter.ContextId -> {
                    checkTextCriteria(criteria, message.extendedHeader?.contextId)
                }

                FilterParameter.AppId -> {
                    checkTextCriteria(criteria, message.extendedHeader?.applicationId)
                }

                FilterParameter.SessionId -> {
                    message.standardHeader.sessionId == criteria.value.toInt()
                }

                FilterParameter.Payload -> {
                    checkTextCriteria(criteria, message.payloadText())
                }
            }
        }

    }

    companion object {
        val Empty = ColorFilter("New filter", mutableMapOf(), CellStyle.Default)
    }
}

val ColorFilterWarn = ColorFilter(
    "Warn",
    mapOf(
        FilterParameter.MessageTypeInfo to FilterCriteria(
            MessageTypeInfo.DLT_LOG_WARN.name,
            TextCriteria.PlainText
        )
    ),
    CellStyle(backgroundColor = Color.Yellow)
)
val ColorFilterError = ColorFilter(
    "Error",
    mapOf(
        FilterParameter.MessageTypeInfo to FilterCriteria(
            MessageTypeInfo.DLT_LOG_DLT_ERROR.name,
            TextCriteria.PlainText
        )
    ),
    CellStyle(backgroundColor = Color(0xE7, 0x62, 0x29), textColor = Color.White)
)
val ColorFilterFatal = ColorFilter(
    "Fatal",
    mapOf(
        FilterParameter.MessageTypeInfo to FilterCriteria(
            MessageTypeInfo.DLT_LOG_FATAL.name,
            TextCriteria.PlainText
        )
    ),
    CellStyle(backgroundColor = Color(0xE7, 0x62, 0x29), textColor = Color.White)
)

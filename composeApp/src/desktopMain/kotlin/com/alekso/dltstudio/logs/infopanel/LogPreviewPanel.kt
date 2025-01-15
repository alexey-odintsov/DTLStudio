package com.alekso.dltstudio.logs.infopanel

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.logs.insights.LogInsight
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.model.VirtualDevice
import com.alekso.dltstudio.ui.Panel
import com.alekso.dltstudio.ui.TabsPanel
import com.alekso.dltstudio.utils.SampleData

@Composable
fun LogPreviewPanel(
    modifier: Modifier,
    logMessage: LogMessage?,
    virtualDevices: List<VirtualDevice>,
    messageIndex: Int,
    logInsights: SnapshotStateList<LogInsight>? = null,
    onCommentUpdated: (LogMessage, String?) -> Unit = { _, _ -> },
    onShowVirtualDeviceClicked: () -> Unit = {},
) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabClickListener: (Int) -> Unit = { i -> tabIndex = i }

    Column(modifier = modifier.background(MaterialTheme.colors.background)) {
        Panel(Modifier.fillMaxSize(), title = "Message Info") {
            TabsPanel(tabIndex, listOf("Simple", "Detailed", "Preview"), tabClickListener)

            when (tabIndex) {
                0 -> {
                    DLTSimplifiedInfoView(
                        logMessage = logMessage, messageIndex = messageIndex,
                        insights = logInsights,
                        onCommentUpdated = onCommentUpdated
                    )
                }
                1 -> {
                    DLTDetailedInfoView(logMessage = logMessage, messageIndex = messageIndex)
                }

                2 -> {
                    DevicePreviewView(
                        modifier = Modifier.fillMaxSize(), logMessage = logMessage,
                        virtualDevices = virtualDevices,
                        messageIndex = messageIndex,
                        onShowVirtualDeviceClicked = onShowVirtualDeviceClicked,
                    )
                }

                else -> {}
            }

        }
    }
}


@Composable
fun TableRow(col1Width: Int, col1Value: String, col2Value: String?) {
    val cellBackground = Color(250, 250, 250)

    Row(modifier = Modifier.background(Color.LightGray)) {
        if (col1Width != 0) {
            Box(
                modifier = Modifier.padding(end = 1.dp, bottom = 1.dp)
                    .background(color = cellBackground)
            ) {
                MonoText(
                    modifier = Modifier.width(col1Width.dp).padding(start = 2.dp, end = 2.dp),
                    text = col1Value
                )
            }
        }
        Box(
            modifier = Modifier.weight(1f).padding(end = 1.dp, bottom = 1.dp)
                .background(color = cellBackground)
        ) {
            MonoText(
                modifier = Modifier.padding(start = 2.dp, end = 2.dp),
                text = col2Value ?: ""
            )
        }
    }
}

@Composable
fun Header(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier.padding(top = 4.dp),
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight(600),
        fontSize = 11.sp,
        text = text
    )
    Divider()
}

@Composable
fun MonoText(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        text = text
    )
}

@Preview
@Composable
fun PreviewLogPreview() {
    val dltMessage = LogMessage(SampleData.getSampleDltMessages(1)[0])
    LogPreviewPanel(
        Modifier.width(200.dp),
        logMessage = dltMessage,
        virtualDevices = emptyList(),
        0
    )
}

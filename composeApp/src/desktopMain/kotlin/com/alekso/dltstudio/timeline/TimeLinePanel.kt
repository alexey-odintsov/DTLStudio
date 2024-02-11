package com.alekso.dltstudio.timeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.ParseSession
import com.alekso.dltstudio.cpu.CPUCLegend
import com.alekso.dltstudio.cpu.CPUSLegend
import com.alekso.dltstudio.cpu.CPUSView
import com.alekso.dltstudio.cpu.CPUUsageView
import com.alekso.dltstudio.memory.MemoryLegend
import com.alekso.dltstudio.memory.MemoryView
import com.alekso.dltstudio.user.UserStateLegend
import com.alekso.dltstudio.user.UserStateView
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
private val simpleTimeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.ENGLISH)
private val LEGEND_WIDTH_DP = 250.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimeLinePanel(
    modifier: Modifier,
    dltSession: ParseSession?,
    progressCallback: (Float) -> Unit,
    offsetSec: Float,
    offsetUpdate: (Float) -> Unit,
    scale: Float,
    scaleUpdate: (Float) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var cursorPosition by remember { mutableStateOf(Offset(0f, 0f)) }
    var secSizePx by remember { mutableStateOf(1f) }

    val dragCallback = { pe: PointerEvent, width: Int ->
        if (dltSession != null) {
            val secSize: Float = width / (dltSession.totalSeconds.toFloat())
            val dragAmount = pe.changes.first().position.x - pe.changes.first().previousPosition.x
            offsetUpdate(offsetSec + (dragAmount / secSize / scale))
        }
    }

    Column(modifier = modifier) {
        TimelineToolbar(
            leftClick = { offsetUpdate(offsetSec + 1f) },
            rightClick = { offsetUpdate(offsetSec - 1f) },
            zoomInClick = { scaleUpdate(scale + 1f) },
            zoomOutClick = { scaleUpdate(scale - 1f) },
            zoomFitClick = {
                scaleUpdate(1f)
                offsetUpdate(0f)
            },
            runClick = {
                if (dltSession != null) {
                    coroutineScope.launch {
                        TimelineAnalyzer.analyzeTimeline(dltSession, progressCallback)
                    }
                }
            })

        Divider()

        if (dltSession != null) {

            Text(
                "Time range: ${simpleDateFormat.format(dltSession.timeStart)} .. ${
                    simpleDateFormat.format(dltSession.timeEnd)
                }"
            )
            Text("Offset: ${"%.2f".format(offsetSec)}; scale: ${"%.2f".format(scale)}")

            Row {
                Box(modifier = Modifier.width(LEGEND_WIDTH_DP))
                TimeRuler(
                    Modifier.fillMaxWidth(1f),
                    offsetSec,
                    scale,
                    dltSession
                )
            }

            val state = rememberLazyListState()
            val panels = mutableStateListOf<@Composable () -> Unit>(
                {
                    Row {
                        TimelineLegend(
                            modifier = Modifier.width(LEGEND_WIDTH_DP).height(200.dp),
                            title = "CPU usage by process",
                            map = dltSession.userEntries
                        )
                        TimelinePercentageView(
                            offsetSec = offsetSec,
                            scale = scale,
                            modifier = Modifier.height(200.dp).fillMaxWidth()
                                .onPointerEvent(
                                    PointerEventType.Move,
                                    onEvent = { dragCallback(it, size.width) }),
                            map = dltSession.userEntries,
                            dltSession = dltSession
                        )
                    }
                },
                {
                    Row {
                        UserStateLegend(
                            modifier = Modifier.width(LEGEND_WIDTH_DP).height(100.dp),
                            map = dltSession.userStateEntries
                        )
                        UserStateView(
                            offset = offsetSec,
                            scale = scale,
                            modifier = Modifier.height(100.dp).fillMaxWidth()
                                .onPointerEvent(
                                    PointerEventType.Move,
                                    onEvent = { dragCallback(it, size.width) }),
                            map = dltSession.userStateEntries,
                            dltSession = dltSession
                        )
                    }
                },
                {
                    Row {
                        CPUCLegend(
                            modifier = Modifier.width(LEGEND_WIDTH_DP).height(300.dp),
                            items = dltSession.cpuUsage
                        )
                        CPUUsageView(
                            offset = offsetSec,
                            scale = scale,
                            modifier = Modifier.height(300.dp).fillMaxWidth()
                                .onPointerEvent(
                                    PointerEventType.Move,
                                    onEvent = { dragCallback(it, size.width) }),
                            items = dltSession.cpuUsage,
                            dltSession = dltSession
                        )
                    }
                },
                {
                    Row {
                        CPUSLegend(
                            modifier = Modifier.width(LEGEND_WIDTH_DP).height(300.dp),
                            items = dltSession.cpus
                        )
                        CPUSView(
                            offset = offsetSec,
                            scale = scale,
                            modifier = Modifier.height(300.dp).fillMaxWidth()
                                .onPointerEvent(
                                    PointerEventType.Move,
                                    onEvent = { dragCallback(it, size.width) }),
                            items = dltSession.cpus,
                            dltSession = dltSession
                        )
                    }
                },
                {
                    Row {
                        MemoryLegend(
                            modifier = Modifier.width(LEGEND_WIDTH_DP).height(300.dp),
                            map = dltSession.memt
                        )
                        MemoryView(
                            modifier = Modifier.height(300.dp).fillMaxWidth()
                                .onPointerEvent(
                                    PointerEventType.Move,
                                    onEvent = { dragCallback(it, size.width) }),

                            map = dltSession.memt,
                            offset = offsetSec,
                            scale = scale,
                            dltSession = dltSession
                        )
                    }
                }
            )
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(Modifier.onPointerEvent(
                    eventType = PointerEventType.Move,
                    onEvent = { event ->
                        cursorPosition = event.changes[0].position
                    }).onSizeChanged {
                    //secSizePx = ((it.width - LEGEND_WIDTH_DP) * scale) / (dltSession.totalSeconds)
                }, state
                ) {
                    items(panels.size) { i ->
                        if (i > 0) {
                            Divider(Modifier.padding(2.dp))
                        }
                        panels[i].invoke()
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = state
                    )
                )
                val textMeasurer = rememberTextMeasurer()
                Canvas(modifier = modifier.fillMaxSize().clipToBounds()) {
                    if (cursorPosition.x < LEGEND_WIDTH_DP.toPx()) return@Canvas
                    secSizePx =
                        ((size.width - LEGEND_WIDTH_DP.toPx()) * scale) / dltSession.totalSeconds.toFloat()

                    drawLine(
                        Color(0xFFFFFFc0),
                        Offset(cursorPosition.x, 0f),
                        Offset(cursorPosition.x, size.height)
                    )
                    val cursorOffsetSec: Float =
                        ((cursorPosition.x - LEGEND_WIDTH_DP.toPx()) / secSizePx) - offsetSec
                    val cursorTimestamp: Long =
                        (1000L * cursorOffsetSec).toLong() + dltSession.timeStart

                    drawText(
                        textMeasurer,
                        text = "${simpleTimeFormat.format(cursorTimestamp)} (${"%+.2f".format(cursorOffsetSec)})",
                        topLeft = Offset(cursorPosition.x + 4.dp.toPx(), 4.dp.toPx()),
                        style = TextStyle(color = Color.Yellow, fontSize = 10.sp, background = Color(0x80808080))
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewTimeline() {
    TimeLinePanel(
        Modifier.fillMaxWidth().height(600.dp),
        dltSession = ParseSession({}, listOf(File(""))),
        progressCallback = { },
        offsetSec = 0f,
        offsetUpdate = {},
        scale = 1f,
        scaleUpdate = {},
    )
}
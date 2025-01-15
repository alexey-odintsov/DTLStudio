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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.TimeFormatter
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.timeline.filters.TimelineFiltersDialog
import com.alekso.dltstudio.timeline.graph.TimelineDurationView
import com.alekso.dltstudio.timeline.graph.TimelineEventView
import com.alekso.dltstudio.timeline.graph.TimelineMinMaxValueView
import com.alekso.dltstudio.timeline.graph.TimelinePercentageView
import com.alekso.dltstudio.timeline.graph.TimelineSingleStateView
import com.alekso.dltstudio.timeline.graph.TimelineStateView
import com.alekso.dltstudio.utils.SampleData

private val LEGEND_WIDTH_DP = 250.dp
private val TIME_MARKER_WIDTH_DP = 140.dp
private val TIME_MARKER_HEIGHT_DP = 12.dp
private const val MOVE_TIMELINE_STEP_PX = 10

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimeLinePanel(
    modifier: Modifier,
    timelineViewModel: TimelineViewModel,
    logMessages: SnapshotStateList<LogMessage>,
    offsetSec: Float,
    offsetUpdate: (Float) -> Unit,
    scale: Float,
    scaleUpdate: (Float) -> Unit,
) {
    var cursorPosition by remember { mutableStateOf(Offset(0f, 0f)) }
    var secSizePx by remember { mutableStateOf(1f) }

    val dragCallback = { pe: PointerEvent, width: Int ->
        if (logMessages.isNotEmpty()) {
            val secSize: Float = width / (timelineViewModel.totalSeconds.toFloat())
            val dragAmount = pe.changes.first().position.x - pe.changes.first().previousPosition.x
            offsetUpdate(offsetSec + (dragAmount / secSize / scale))
        }
    }
    val dialogState = remember { mutableStateOf(false) }

    Column(modifier = modifier.onKeyEvent { e ->
        if (e.type == KeyEventType.KeyDown) {
            when (e.key) {
                Key.A -> {
                    offsetUpdate(offsetSec + MOVE_TIMELINE_STEP_PX / secSizePx)
                    true
                }

                Key.D -> {
                    offsetUpdate(offsetSec - MOVE_TIMELINE_STEP_PX / secSizePx)
                    true
                }

                Key.W -> {
                    scaleUpdate(scale + 1f)
                    true
                }

                Key.S -> {
                    scaleUpdate(scale - 1f)
                    true
                }

                else -> {
                    false
                }
            }
        } else false
    }) {
        TimelineToolbar(
            leftClick = { offsetUpdate(offsetSec + MOVE_TIMELINE_STEP_PX / secSizePx) },
            rightClick = { offsetUpdate(offsetSec - MOVE_TIMELINE_STEP_PX / secSizePx) },
            zoomInClick = { scaleUpdate(scale + 1f) },
            zoomOutClick = { scaleUpdate(scale - 1f) },
            zoomFitClick = {
                scaleUpdate(1f)
                offsetUpdate(0f)
            },
            analyzeState = timelineViewModel.analyzeState.value,
            onAnalyzeClick = { timelineViewModel.onAnalyzeClicked(logMessages) },
            onTimelineFiltersClicked = { dialogState.value = true },
        )

        if (dialogState.value) {
            TimelineFiltersDialog(
                visible = dialogState.value,
                onDialogClosed = { dialogState.value = false },
                timelineFilters = timelineViewModel.timelineFilters,
                onTimelineFilterUpdate = { i, f -> timelineViewModel.onTimelineFilterUpdate(i, f) },
                onTimelineFilterDelete = { timelineViewModel.onTimelineFilterDelete(it) },
                onTimelineFilterMove = { i, o -> timelineViewModel.onTimelineFilterMove(i, o) },
            )
        }

        Divider()

        if (logMessages.isNotEmpty()) {
            val timeFrame = TimeFrame(
                timestampStart = timelineViewModel.timeStart,
                timestampEnd = timelineViewModel.timeEnd,
                scale = scale,
                offsetSeconds = offsetSec
            )

            Text(
                "Time range: ${TimeFormatter.formatDateTime(timelineViewModel.timeStart)} .. ${
                    TimeFormatter.formatDateTime(timelineViewModel.timeEnd)
                }"
            )
            Text("Offset: ${"%.2f".format(offsetSec)}; scale: ${"%.2f".format(scale)}")

            Row {
                Box(modifier = Modifier.width(LEGEND_WIDTH_DP))
                TimeRuler(
                    Modifier.fillMaxWidth(1f),
                    offsetSec,
                    scale,
                    timeStart = timelineViewModel.timeStart,
                    timeEnd = timelineViewModel.timeEnd,
                    totalSeconds = timelineViewModel.totalSeconds
                )
            }

            val state = rememberLazyListState()
            val panels = mutableStateListOf<@Composable () -> Unit>()

            timelineViewModel.timelineFilters.forEachIndexed { index, timelineFilter ->
                if (timelineFilter.enabled) {
                    panels.add {
                        Row {
                            TimelineLegend(
                                modifier = Modifier.width(LEGEND_WIDTH_DP).height(200.dp),
                                title = timelineFilter.name,
                                entries = timelineViewModel.entriesMap[timelineFilter.key],
                                { key ->
                                    timelineViewModel.highlightedKeysMap[timelineFilter.key] = key
                                },
                                highlightedKey = timelineViewModel.highlightedKeysMap[timelineFilter.key]
                            )
                            when (timelineFilter.diagramType) {
                                DiagramType.Percentage -> {
                                    TimelinePercentageView(
                                        modifier = Modifier.height(200.dp).fillMaxWidth()
                                            .onPointerEvent(
                                                PointerEventType.Move,
                                                onEvent = { dragCallback(it, size.width) }),
                                        entries = timelineViewModel.retrieveEntriesForFilter(timelineFilter) as TimeLinePercentageEntries?,
                                        timeFrame = timeFrame,
                                        highlightedKey = timelineViewModel.highlightedKeysMap[timelineFilter.key]
                                    )
                                }

                                DiagramType.MinMaxValue -> {
                                    TimelineMinMaxValueView(
                                        modifier = Modifier.height(200.dp).fillMaxWidth()
                                            .onPointerEvent(
                                                PointerEventType.Move,
                                                onEvent = { dragCallback(it, size.width) }),
                                        entries = timelineViewModel.retrieveEntriesForFilter(timelineFilter) as TimeLineMinMaxEntries?,
                                        timeFrame = timeFrame,
                                        highlightedKey = timelineViewModel.highlightedKeysMap[timelineFilter.key]
                                    )
                                }

                                DiagramType.State -> {
                                    TimelineStateView(
                                        modifier = Modifier.height(200.dp).fillMaxWidth()
                                            .onPointerEvent(
                                                PointerEventType.Move,
                                                onEvent = { dragCallback(it, size.width) }),
                                        entries = timelineViewModel.retrieveEntriesForFilter(timelineFilter) as TimeLineStateEntries?,
                                        timeFrame = timeFrame,
                                        highlightedKey = timelineViewModel.highlightedKeysMap[timelineFilter.key]
                                    )
                                }

                                DiagramType.SingleState -> {
                                    TimelineSingleStateView(
                                        modifier = Modifier.height(200.dp).fillMaxWidth()
                                            .onPointerEvent(
                                                PointerEventType.Move,
                                                onEvent = { dragCallback(it, size.width) }),
                                        entries = timelineViewModel.retrieveEntriesForFilter(timelineFilter) as TimeLineSingleStateEntries?,
                                        timeFrame = timeFrame,
                                        highlightedKey = timelineViewModel.highlightedKeysMap[timelineFilter.key]
                                    )
                                }

                                DiagramType.Duration -> {
                                    TimelineDurationView(
                                        modifier = Modifier.height(200.dp).fillMaxWidth()
                                            .onPointerEvent(
                                                PointerEventType.Move,
                                                onEvent = { dragCallback(it, size.width) }),
                                        entries = timelineViewModel.retrieveEntriesForFilter(timelineFilter) as TimeLineDurationEntries?,
                                        timeFrame = timeFrame,
                                        highlightedKey = timelineViewModel.highlightedKeysMap[timelineFilter.key]
                                    )
                                }

                                DiagramType.Events -> TimelineEventView(
                                    modifier = Modifier.height(200.dp).fillMaxWidth()
                                        .onPointerEvent(
                                            PointerEventType.Move,
                                            onEvent = { dragCallback(it, size.width) }),
                                    entries = timelineViewModel.retrieveEntriesForFilter(timelineFilter) as TimeLineEventEntries?,
                                    timeFrame = timeFrame,
                                    highlightedKey = timelineViewModel.highlightedKeysMap[timelineFilter.key]
                                )
                            }
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    Modifier.onPointerEvent(
                        eventType = PointerEventType.Move,
                        onEvent = { event ->
                            cursorPosition = event.changes[0].position
                        }), state
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
                        ((size.width - LEGEND_WIDTH_DP.toPx()) * scale) / timelineViewModel.totalSeconds.toFloat()

                    val doesMarkerFit =
                        (size.width - cursorPosition.x) > TIME_MARKER_WIDTH_DP.toPx()

                    drawLine(
                        Color(0xFFFFFFc0),
                        Offset(cursorPosition.x, 0f),
                        Offset(cursorPosition.x, size.height)
                    )
                    val cursorOffsetSec: Float =
                        ((cursorPosition.x - LEGEND_WIDTH_DP.toPx()) / secSizePx) - offsetSec
                    val cursorTimestamp: Long =
                        (1000000L * cursorOffsetSec).toLong() + timelineViewModel.timeStart

                    drawText(
                        size = Size(TIME_MARKER_WIDTH_DP.toPx(), TIME_MARKER_HEIGHT_DP.toPx()),
                        textMeasurer = textMeasurer,
                        text = "${TimeFormatter.formatTime(cursorTimestamp)} (${
                            "%+.2f".format(
                                cursorOffsetSec
                            )
                        })",
                        topLeft = Offset(
                            if (doesMarkerFit) cursorPosition.x + 4.dp.toPx() else cursorPosition.x - TIME_MARKER_WIDTH_DP.toPx() - 4.dp.toPx(),
                            4.dp.toPx()
                        ),
                        style = TextStyle(
                            color = Color.Yellow,
                            fontSize = 10.sp,
                            background = Color(0x80808080),
                            textAlign = if (doesMarkerFit) TextAlign.Left else TextAlign.Right
                        ), overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewTimeline() {
    val list = SnapshotStateList<LogMessage>()
    list.addAll(SampleData.getSampleDltMessages(20).map { LogMessage(it) })
    TimeLinePanel(
        Modifier.fillMaxWidth().height(600.dp),
        logMessages = list,
        timelineViewModel = TimelineViewModel({}),
        offsetSec = 0f,
        offsetUpdate = {},
        scale = 1f,
        scaleUpdate = { f -> },
    )
}
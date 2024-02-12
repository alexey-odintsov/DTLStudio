package com.alekso.dltstudio.timeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.colors.ColorPalette
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale


private const val SERIES_COUNT = 10

@Composable
fun TimelineMinMaxValueView(
    modifier: Modifier,
    entries: TimelineMinMaxEntries?,
    timeFrame: TimeFrame,
    splitTimeSec: Float = 999f,
    seriesPostfix: String = "",
    showVerticalSeries: Boolean = false
) {
    val textMeasurer = rememberTextMeasurer()
    val seriesTextStyle = remember { TextStyle(color = Color.LightGray, fontSize = 10.sp) }

    Canvas(modifier = modifier.background(Color.Gray).clipToBounds()) {
        val height = size.height
        val width = size.width
        val secSizePx: Float = timeFrame.calculateSecSizePx(width)

        if (entries == null) return@Canvas
        entries.minValue = 0f

        // Draw series lines
        val step = (entries.maxValue - entries.minValue) / SERIES_COUNT
        for (i in 0..SERIES_COUNT) {
            val y = height * i / SERIES_COUNT
            drawLine(Color.LightGray, Offset(0f, y), Offset(width, y), alpha = 0.5f)
            drawText(
                textMeasurer,
                text = "${"%.0f".format(entries.maxValue - (i * step))}$seriesPostfix",
                topLeft = Offset(3.dp.toPx(), height * i / SERIES_COUNT),
                style = seriesTextStyle
            )
        }

        if (showVerticalSeries) {
            for (i in 0..timeFrame.getTotalSeconds()) {
                val x = timeFrame.offsetSeconds * secSizePx + i * secSizePx
                drawLine(Color.LightGray, Offset(x, 0f), Offset(x, height), alpha = 0.2f)
            }
        }

        // Draw values
        val map = entries.getEntriesMap()
        map.keys.forEachIndexed { index, key ->
            val items = map[key]
            items?.forEachIndexed entriesIteration@{ i, entry ->
                if (i == 0) return@entriesIteration

                val prev = items[i - 1]
                val prevDiffSec = (entry.timestamp - prev.timestamp) / 1000f
                // split lines if difference is too big
                if (prevDiffSec > splitTimeSec) {
                    return@entriesIteration
                }
                val prevX = (prev.timestamp - timeFrame.timestampStart) / 1000f * secSizePx
                val prevY = height - height * prev.value.toFloat() / entries.maxValue

                val curX = ((entry.timestamp - timeFrame.timestampStart) / 1000f * secSizePx)
                val curY = height - height * entry.value.toFloat() / entries.maxValue

                drawLine(
                    ColorPalette.getColor(index),
                    Offset(timeFrame.offsetSeconds * secSizePx + prevX, prevY),
                    Offset(timeFrame.offsetSeconds * secSizePx + curX, curY),
                    strokeWidth = 1.dp.toPx(),
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewTimelineMinMaxValueView() {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
    val ts = Instant.now().toEpochMilli()
    val te = ts + 7000

    val entries = TimelineMinMaxEntries()
    entries.maxValue = 151f
    entries.minValue = 33f
    entries.entries["1325"] = mutableListOf(
        TimelineEntry(ts + 1450, "1325", "110"),
        TimelineEntry(ts + 2000, "1325", "83"),
        TimelineEntry(ts + 3300, "1325", "127"),
        TimelineEntry(ts + 4400, "1325", "89"),
    )
    entries.entries["435"] = mutableListOf(
        TimelineEntry(ts + 200, "435", "133"),
        TimelineEntry(ts + 2100, "435", "151"),
        TimelineEntry(ts + 2700, "435", "104"),
        TimelineEntry(ts + 3400, "435", "42"),
        TimelineEntry(ts + 3560, "435", "63"),
        TimelineEntry(ts + 4000, "435", "72"),
        TimelineEntry(ts + 6800, "435", "111"),
    )

    Column {
        for (i in 1..3) {
            val timeFrame = TimeFrame(
                timestampStart = ts,
                timestampEnd = te,
                scale = i.toFloat(),
                offsetSeconds = 0f
            )

            Text(text = "start: ${simpleDateFormat.format(ts)}")
            Text(text = "end: ${simpleDateFormat.format(te)}")
            Text(text = "seconds: ${timeFrame.getTotalSeconds()}")
            TimelineMinMaxValueView(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                entries = entries,
                timeFrame = timeFrame,
                seriesPostfix = " Mb"
            )
        }
    }
}
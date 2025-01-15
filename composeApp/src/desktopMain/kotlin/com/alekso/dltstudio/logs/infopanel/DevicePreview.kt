package com.alekso.dltstudio.logs.infopanel

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.model.VirtualDevice
import com.alekso.dltstudio.ui.CustomButton
import com.alekso.dltstudio.ui.CustomDropDown
import com.alekso.dltstudio.utils.SampleData

@Composable
fun DevicePreviewView(
    modifier: Modifier = Modifier,
    virtualDevices: List<VirtualDevice>,
    logMessage: LogMessage?,
    messageIndex: Int,
    onShowVirtualDeviceClicked: () -> Unit = {},
) {
    var deviceViews: List<DeviceView>? = null
    val paddingModifier = Modifier.padding(start = 4.dp, end = 4.dp)

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        logMessage?.dltMessage?.let { message ->
            Header(
                modifier = paddingModifier, text = "Device Preview for #$messageIndex:"
            )
//            val headerText =
//                "${TimeFormatter.formatDateTime(it.timeStampNano)} " + "${it.extendedHeader?.applicationId} " + "${it.extendedHeader?.contextId} "
//            TableRow(0, "", headerText)
//            TableRow(0, "", it.payload)
//        }
            deviceViews = DeviceView.parse(message.payloadText() ?: "")
            if (deviceViews.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.padding(start = 2.dp, end = 2.dp),
                    text = "No preview found",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                )
            }
        }

        var currentDevice by rememberSaveable { mutableStateOf(virtualDevices.firstOrNull()) }
        CustomButton(
            modifier = Modifier.padding(0.dp),
            onClick = { onShowVirtualDeviceClicked() },
        ) {
            Text("Manage devices")
        }

        if (currentDevice != null) {
            CustomDropDown(
                modifier = Modifier.width(200.dp).padding(horizontal = 4.dp),
                items = virtualDevices.map { device -> "${device.name}: ${device.width}x${device.height}" },
                initialSelectedIndex = 0,
                onItemsSelected = { i ->
                    currentDevice = virtualDevices[i]
                })

            Row(modifier = Modifier.weight(1f).fillMaxWidth().background(Color.Gray)) {
                VirtualDevicePreview(
                    modifier = Modifier.fillMaxSize(),
                    deviceSize = Size(
                        currentDevice!!.width.toFloat(),
                        currentDevice!!.height.toFloat()
                    ),
                    deviceViews = deviceViews,
                )
            }
        } else {
            Text(
                "No virtual device found, please create one",
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewDevicePreview() {
    val dltMessage = SampleData.create(
        payloadText = "TestView[2797]: onGlobalFocusChanged: oldFocus:com.ui.custom.ProgressBarFrameLayout{f5e8f76 VFE...CL. ......ID 2298,22-2835,709 #7f090453 app:id/theme_container aid=1073741849}, newFocus:com.android.car.ui.FocusParkingView{736743f VFED..... .F...... 0,0-1,1 #7f090194 app:id/focus_parking_view aid=1073741832} {bounds:Rect(0, 0 - 342, 240),hasBoundsTransaction,}"
    )
    DevicePreviewView(
        modifier = Modifier.fillMaxSize(),
        logMessage = LogMessage(dltMessage),
        messageIndex = 0,
        virtualDevices = emptyList()
    )
}
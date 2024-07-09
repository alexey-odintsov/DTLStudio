package com.alekso.dltstudio.logs.infopanel

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.alekso.dltstudio.ui.CustomButton
import com.alekso.dltstudio.ui.ImageButton
import dtlstudio.composeapp.generated.resources.Res
import dtlstudio.composeapp.generated.resources.icon_delete
import dtlstudio.composeapp.generated.resources.icon_edit


@Composable
fun VirtualDevicesDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    colorFilters: List<VirtualDevice>,
    onColorFilterDelete: (Int) -> Unit,
    onVirtualDeviceUpdate: (VirtualDevice) -> Unit,
) {
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = "Virtual Devices",
        state = rememberDialogState(width = 500.dp, height = 500.dp)
    ) {
        val editDialogState = remember { mutableStateOf(EditVirtualDeviceDialogState(false)) }

        if (editDialogState.value.visible) {
            EditVirtualDeviceDialog(
                visible = editDialogState.value.visible,
                onDialogClosed = { editDialogState.value = EditVirtualDeviceDialogState(false) },
                device = editDialogState.value.device,
                onItemUpdate = { virtualDevice ->
                    editDialogState.value.device = virtualDevice
                    onVirtualDeviceUpdate(virtualDevice)
                }
            )
        }

        VirtualDevicesPanel(
            modifier = Modifier.fillMaxSize(),
            items = colorFilters,
            onEditItemClick = { filter ->
                editDialogState.value = EditVirtualDeviceDialogState(true, filter)
            },
            onItemDelete = { i ->
                onColorFilterDelete(i)
            },
        )
    }
}

@Composable
fun VirtualDevicesPanel(
    modifier: Modifier,
    items: List<VirtualDevice>,
    onEditItemClick: (VirtualDevice) -> Unit,
    onItemDelete: (Int) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize().padding(4.dp)) {
        LazyColumn(Modifier.weight(1f).fillMaxWidth()) {
            itemsIndexed(items = items, key = { i, it -> it.id }) { i, item ->
                Row(
                    Modifier.padding(horizontal = 4.dp, vertical = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = item.name,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    Box(
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "${item.size.width.toInt()}x${item.size.height.toInt()}",
                            modifier = Modifier.wrapContentWidth(),
                        )
                    }

                    ImageButton(modifier = Modifier.size(28.dp),
                        icon = Res.drawable.icon_edit,
                        title = "Edit",
                        onClick = {
                            onEditItemClick(item)
                        })

                    ImageButton(modifier = Modifier.size(28.dp),
                        icon = Res.drawable.icon_delete,
                        title = "Delete",
                        onClick = {
                            onItemDelete(i)
                        })
                }
            }
        }

        CustomButton(
            modifier = Modifier,
            onClick = { onEditItemClick(VirtualDevice.Empty) },
        ) {
            Text("Add device")
        }
    }
}


@Preview
@Composable
fun PreviewVirtualDevicesDialog() {
    val virtualDevices = mutableListOf(
        VirtualDevice(1, "Square", Size(1500f, 1500f)),
        VirtualDevice(2, "Wide", Size(2000f, 900f)),
    )

    VirtualDevicesPanel(
        modifier = Modifier.height(400.dp).fillMaxWidth(),
        virtualDevices,
        { _ -> },
        { _ -> },
    )
}
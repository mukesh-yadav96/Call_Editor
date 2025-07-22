package com.reign.calleditor.ui.util

import android.provider.CallLog
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.reign.calleditor.model.CallLogEntry
import com.reign.calleditor.model.EditCallLogUiState
import com.reign.calleditor.routes.NavigationItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun rememberEditCallLogState(currentEntry: CallLogEntry?): EditCallLogUiState {
    val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val date = currentEntry?.date?.let { sdfDate.format(Date(it)) } ?: ""
    val time = currentEntry?.date?.let { sdfTime.format(Date(it)) } ?: ""

    return remember {
        EditCallLogUiState(
            name = currentEntry?.name ?: "",
            number = currentEntry?.number ?: "",
            date = date,
            time = time,
            durationText = currentEntry?.duration?.let {
                String.format("%02d:%02d:%02d", it / 3600, (it % 3600) / 60, it % 60)
            } ?: "00:00:00",
            callType = currentEntry?.type ?: CallLog.Calls.INCOMING_TYPE
        )
    }
}

@Composable
fun getCallTypeVisuals(type: Int): Pair<ImageVector, Color> {
    return when (type) {
        CallLog.Calls.INCOMING_TYPE -> Icons.AutoMirrored.Filled.CallReceived to MaterialTheme.colorScheme.primary
        CallLog.Calls.OUTGOING_TYPE -> Icons.AutoMirrored.Filled.CallMade to MaterialTheme.colorScheme.secondary
        CallLog.Calls.MISSED_TYPE -> Icons.AutoMirrored.Filled.CallMissed to MaterialTheme.colorScheme.error
        CallLog.Calls.REJECTED_TYPE -> Icons.AutoMirrored.Filled.CallMissed to MaterialTheme.colorScheme.onErrorContainer
        // Add other types as needed
        else -> Icons.AutoMirrored.Filled.CallReceived to MaterialTheme.colorScheme.onSurfaceVariant
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCallLogFAB(navController: NavController) {
    val tooltipState = rememberTooltipState()
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip { Text("Add Call Log") }
        },
        state = tooltipState
    ) {
        ExtendedFloatingActionButton(
            onClick = {
                navController.navigate(route = NavigationItem.EditCallHistory.baseRoute)
            },
            icon = {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Call Log",
                    modifier = Modifier.size(28.dp)
                )
            },
            text = {
                Text("Add Call Log", fontSize = 18.sp)
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = FloatingActionButtonDefaults.extendedFabShape
        )
    }
}

package com.reign.calleditor.ui.util

import android.provider.CallLog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.reign.calleditor.model.CallLogEntry
import com.reign.calleditor.model.EditCallLogUiState
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

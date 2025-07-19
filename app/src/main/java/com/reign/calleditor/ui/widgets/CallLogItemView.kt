package com.reign.calleditor.ui.widgets

import android.provider.CallLog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.reign.calleditor.model.CallLogEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CallLogItemView(
    modifier: Modifier = Modifier,
    log: CallLogEntry
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // TODO: Add an icon based on call type (incoming, outgoing, missed)
            // Example:
            // Icon(imageVector = when(log.type) { ... }, contentDescription = null)
            // Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.name ?: log.number ?: "Unknown Number",
                    style = MaterialTheme.typography.titleMedium
                )
                if (log.name != null && log.number != null) { // Show number if name is present
                    Text(
                        text = log.number,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Duration: ${formatDuration(log.duration)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = callLogTypeToString(log.type),
                    style = MaterialTheme.typography.bodySmall,
                    color = getCallTypeColor(log.type)
                )
                Text(
                    text = dateFormat.format(Date(log.date)),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun getCallTypeColor(type: Int): androidx.compose.ui.graphics.Color {
    return when (type) {
        CallLog.Calls.INCOMING_TYPE -> MaterialTheme.colorScheme.primary
        CallLog.Calls.OUTGOING_TYPE -> MaterialTheme.colorScheme.secondary
        CallLog.Calls.MISSED_TYPE -> MaterialTheme.colorScheme.error
        CallLog.Calls.REJECTED_TYPE -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.onSurface
    }
}

fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return when {
        hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, secs)
        minutes > 0 -> String.format("%02d:%02d", minutes, secs)
        else -> String.format("%ds", secs)
    }
}

fun callLogTypeToString(type: Int): String {
    return when (type) {
        CallLog.Calls.INCOMING_TYPE -> "Incoming"
        CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
        CallLog.Calls.MISSED_TYPE -> "Missed"
        CallLog.Calls.VOICEMAIL_TYPE -> "Voicemail"
        CallLog.Calls.REJECTED_TYPE -> "Rejected"
        CallLog.Calls.BLOCKED_TYPE -> "Blocked"
        CallLog.Calls.ANSWERED_EXTERNALLY_TYPE -> "Answered Externally"
        else -> "Unknown"
    }
}

@Preview(showBackground = true)
@Composable
fun CallLogItemViewPreview() {
    CallLogItemView(
        log = CallLogEntry(
            "1",
            "123-456-7890",
            Date().time - 100000,
            CallLog.Calls.INCOMING_TYPE,
            60,
            "John Doe"
        )
    )
}


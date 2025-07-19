package com.reign.calleditor.ui.widgets

import android.provider.CallLog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.reign.calleditor.model.CallLogEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CallLogItemView(log: CallLogEntry, modifier: Modifier = Modifier) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()) } // Shortened format

    val (icon, iconColor) = getCallTypeVisuals(log.type)
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { /* TODO: Handle item click, e.g., navigate to details or show options */ }
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = log.name ?: log.number ?: "Unknown",
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            supportingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (log.name != null && log.number != null) {
                        Text(
                            text = "${log.number} • ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = formatDuration(log.duration),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            leadingContent = {
                Icon(
                    imageVector = icon,
                    contentDescription = callLogTypeToString(log.type),
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
            },
            trailingContent = {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = callLogTypeToString(log.type),
                        style = MaterialTheme.typography.labelSmall,
                        color = iconColor
                    )
                    Text(
                        text = dateFormat.format(Date(log.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant // Or surface
            )
        )
    }
}

@Composable
fun getCallTypeVisuals(type: Int): Pair<ImageVector, Color> {
    return when (type) {
        CallLog.Calls.INCOMING_TYPE -> Icons.AutoMirrored.Filled.CallReceived to MaterialTheme.colorScheme.primary
        CallLog.Calls.OUTGOING_TYPE -> Icons.AutoMirrored.Filled.CallMade to MaterialTheme.colorScheme.secondary
        CallLog.Calls.MISSED_TYPE -> Icons.AutoMirrored.Filled.CallMissed to MaterialTheme.colorScheme.error
        CallLog.Calls.REJECTED_TYPE -> Icons.AutoMirrored.Filled.CallMissed to MaterialTheme.colorScheme.onErrorContainer // Or a specific rejected icon
        // Add other types as needed
        else -> Icons.AutoMirrored.Filled.CallReceived to MaterialTheme.colorScheme.onSurfaceVariant // Default
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
            76,
            "John Doe"
        )
    )
}


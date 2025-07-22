package com.reign.calleditor.ui.widgets

import android.provider.CallLog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.reign.calleditor.model.CallLogEntry
import com.reign.calleditor.routes.NavigationItem
import com.reign.calleditor.ui.util.getCallTypeVisuals
import com.reign.calleditor.util.Helper.callLogTypeToString
import com.reign.calleditor.util.Helper.formatDuration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CallLogItemView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    log: CallLogEntry,
    setCurrentEntry: (CallLogEntry?) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()) }

    val (icon, iconColor) = getCallTypeVisuals(log.type)
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = {
            setCurrentEntry(log)
            navController.navigate(route = NavigationItem.EditCallHistory.baseRoute)
        }
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = log.name?.takeIf { it.isNotEmpty() } ?: "Unknown",
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            supportingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (log.number != null) {
                        Text(
                            text = "${log.number} • ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = formatDuration(log.duration),
                        style = MaterialTheme.typography.bodyMedium,
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
                        style = MaterialTheme.typography.labelMedium,
                        color = iconColor
                    )
                    Text(
                        text = dateFormat.format(Date(log.date)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CallLogItemViewPreview() {
    val navController= rememberNavController()
    CallLogItemView(
        navController = navController,
        log = CallLogEntry(
            "1",
            "123-456-7890",
            Date().time - 100000,
            CallLog.Calls.INCOMING_TYPE,
            76,
            "John Doe"
        ),
        setCurrentEntry = { }
    )
}


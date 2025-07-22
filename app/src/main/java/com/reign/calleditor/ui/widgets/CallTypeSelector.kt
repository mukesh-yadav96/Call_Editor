package com.reign.calleditor.ui.widgets

import android.provider.CallLog
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.reign.calleditor.model.EditCallLogUiState

@Composable
fun CallTypeSelector(state: EditCallLogUiState) {
    val callTypes = listOf(
        CallLog.Calls.INCOMING_TYPE to "Incoming",
        CallLog.Calls.OUTGOING_TYPE to "Outgoing",
        CallLog.Calls.MISSED_TYPE to "Missed"
    )

    Text("Call Type", style = MaterialTheme.typography.titleLarge)

    callTypes.forEach { (value, label) ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = state.callType == value,
                onClick = {
                    state.callType = value
                    if (value == CallLog.Calls.MISSED_TYPE) {
                        state.durationText = "00:00:00"
                    }
                }
            )
            Text(label)
        }
    }
}

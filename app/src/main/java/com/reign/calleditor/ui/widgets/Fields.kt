package com.reign.calleditor.ui.widgets

import android.provider.CallLog
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.reign.calleditor.model.EditCallLogUiState
import com.vanpra.composematerialdialogs.MaterialDialogState

@Composable
fun NameField(state: EditCallLogUiState) {
    OutlinedTextField(
        value = state.name,
        onValueChange = { state.name = it },
        label = { Text("Name") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun NumberField(state: EditCallLogUiState) {
    OutlinedTextField(
        value = state.number,
        onValueChange = { state.number = it },
        label = { Text("Number") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DateField(state: EditCallLogUiState, dialogState: MaterialDialogState) {
    val source = remember { MutableInteractionSource() }

    OutlinedTextField(
        value = state.date,
        onValueChange = {},
        readOnly = true,
        label = { Text("Date (dd/MM/yyyy)") },
        modifier = Modifier.fillMaxWidth(),
        interactionSource = source,
        trailingIcon = { Icon(Icons.Filled.DateRange, "Pick Date") }
    )

    LaunchedEffect(source) {
        source.interactions.collect {
            if (it is PressInteraction.Release) dialogState.show()
        }
    }
}

@Composable
fun TimeField(state: EditCallLogUiState, dialogState: MaterialDialogState) {
    val source = remember { MutableInteractionSource() }

    OutlinedTextField(
        value = state.time,
        onValueChange = {},
        readOnly = true,
        label = { Text("Time (hh:mm a)") },
        modifier = Modifier.fillMaxWidth(),
        interactionSource = source,
        trailingIcon = { Icon(Icons.Filled.AccessTime, "Pick Time") }
    )

    LaunchedEffect(source) {
        source.interactions.collect {
            if (it is PressInteraction.Release) dialogState.show()
        }
    }
}

@Composable
fun DurationField(state: EditCallLogUiState) {
    OutlinedTextField(
        value = state.durationText,
        onValueChange = {
            if (state.callType != CallLog.Calls.MISSED_TYPE) {
                state.durationText = it
            }
        },
        isError = !state.isDurationValid,
        label = { Text("Duration (HH:mm:ss)") },
        modifier = Modifier.fillMaxWidth(),
        enabled = state.callType != CallLog.Calls.MISSED_TYPE,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )

    if (!state.isDurationValid) {
        Text(
            text = "Invalid format. Use HH:mm:ss",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

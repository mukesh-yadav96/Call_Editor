package com.reign.calleditor.ui.widgets

import android.os.Build
import android.provider.CallLog
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.reign.calleditor.model.EditCallLogUiState
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditCallLogForm(
    state: EditCallLogUiState,
    onSubmit: () -> Unit
) {
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    EditCallLogDialogs(
        dateDialogState = dateDialogState,
        timeDialogState = timeDialogState,
        onDateSelected = { state.date = it },
        onTimeSelected = { state.time = it }
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CallTypeSelector(state)
        NameField(state)
        NumberField(state)
        DateField(state, dateDialogState)
        TimeField(state, timeDialogState)
        DurationField(state)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onSubmit() },
            shape = FloatingActionButtonDefaults.extendedFabShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp),
        ) {
            Icon(Icons.Default.Check, contentDescription = "Done")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Done")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun EditCallLogFormPreview() {
    EditCallLogForm(
        state = EditCallLogUiState(
            "Mukesh", "800000", "22/1/2017", "02:02:02", "02:02:03", callType = CallLog.Calls.OUTGOING_TYPE
        ),
        onSubmit = { }
    )
}

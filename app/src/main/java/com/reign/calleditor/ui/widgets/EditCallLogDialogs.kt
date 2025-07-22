package com.reign.calleditor.ui.widgets

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditCallLogDialogs(
    dateDialogState: MaterialDialogState,
    timeDialogState: MaterialDialogState,
    onDateSelected: (String) -> Unit,
    onTimeSelected: (String) -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    MaterialDialog(dialogState = dateDialogState, buttons = {
        positiveButton("OK")
        negativeButton("Cancel")
    }) {
        datepicker(initialDate = LocalDate.now()) {
            onDateSelected(it.format(dateFormatter))
        }
    }

    MaterialDialog(dialogState = timeDialogState, buttons = {
        positiveButton("OK")
        negativeButton("Cancel")
    }) {
        timepicker(initialTime = LocalTime.now(), is24HourClock = false) {
            onTimeSelected(it.format(timeFormatter))
        }
    }
}

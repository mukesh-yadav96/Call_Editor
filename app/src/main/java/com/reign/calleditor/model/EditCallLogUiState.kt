package com.reign.calleditor.model

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class EditCallLogUiState(
    name: String,
    number: String,
    date: String,
    time: String,
    durationText: String = 0.toString(),
    callType: Int
) {
    var name by mutableStateOf(name)
    var number by mutableStateOf(number)
    var date by mutableStateOf(date)
    var time by mutableStateOf(time)
    var durationText by mutableStateOf(durationText)
    var callType by mutableIntStateOf(callType)

    val isDurationValid: Boolean
        get() = durationText.matches(Regex("^\\d{1,2}:\\d{2}:\\d{2}$"))

    val durationInSeconds: Long
        get() = if (isDurationValid) {
            val (h, m, s) = durationText.split(":").map { it.toIntOrNull() ?: 0 }
            h * 3600 + m * 60 + s.toLong()
        } else 0L
}


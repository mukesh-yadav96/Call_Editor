package com.reign.calleditor.ui.screens

import android.provider.CallLog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.reign.calleditor.viewmodel.CallLogViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCallLogsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: CallLogViewModel
) {
    val context = LocalContext.current
    val currentEntry = viewModel.getCurrentlySelectedEntry()
    val isLoading = viewModel.isLoading

    // Editable states
    var nameState by remember { mutableStateOf("") }
    var numberState by remember { mutableStateOf("") }
    var dateState by remember { mutableStateOf("") }
    var timeState by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(currentEntry?.type ?: CallLog.Calls.INCOMING_TYPE) }

    val callTypes = listOf(
        CallLog.Calls.INCOMING_TYPE to "Incoming",
        CallLog.Calls.OUTGOING_TYPE to "Outgoing",
        CallLog.Calls.MISSED_TYPE to "Missed"
    )

    // Load data from ViewModel once
    LaunchedEffect(currentEntry) {
        currentEntry?.let {
            nameState = it.name ?: ""
            numberState = it.number ?: ""
            duration = it.duration.toString()

            val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())

            dateState = sdfDate.format(Date(it.date))
            timeState = sdfTime.format(Date(it.date))
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Edit Call Log - ${nameState.ifBlank { "Unknown" }}")
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Call Type", style = MaterialTheme.typography.titleMedium)
                callTypes.forEach { (value, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedType == value,
                            onClick = {
                                selectedType = value
                                if (value == CallLog.Calls.MISSED_TYPE) {
                                    duration = "0"
                                }
                            }
                        )
                        Text(label)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nameState,
                    onValueChange = { nameState = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = numberState,
                    onValueChange = { numberState = it },
                    label = { Text("Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dateState,
                    onValueChange = { dateState = it },
                    label = { Text("Date (MM/dd/yyyy)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = timeState,
                    onValueChange = { timeState = it },
                    label = { Text("Time (hh:mm a)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = duration,
                    onValueChange = {
                        if (selectedType != CallLog.Calls.MISSED_TYPE) {
                            duration = it
                        }
                    },
                    label = { Text("Duration (sec)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedType != CallLog.Calls.MISSED_TYPE,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        println("Done: Name=$nameState, Number=$numberState, Date=$dateState, Time=$timeState")
                        viewModel.updateCallLog(
                            id = currentEntry?.id ?: return@Button,
                            name = nameState,
                            number = numberState,
                            dateString = dateState,
                            timeString = timeState,
                            duration = duration.toLongOrNull() ?: 0L,
                            type = selectedType,
                            callback = {
                                navController.popBackStack()
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Done")
                }
            }
        }
    }
}

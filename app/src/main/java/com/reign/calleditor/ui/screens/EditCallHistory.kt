package com.reign.calleditor.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.provider.CallLog
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.reign.calleditor.R
import com.reign.calleditor.model.CallLogEntry
import com.reign.calleditor.ui.theme.CallEditorTheme
import com.reign.calleditor.ui.widgets.CallLogItemView
import com.reign.calleditor.viewmodel.CallLogViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCallHistoryScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    callLogViewModel: CallLogViewModel = viewModel()
) {
    val context = LocalContext.current

    val hasReadPermission = callLogViewModel.hasReadCallLogPermission
    val hasWritePermission = callLogViewModel.hasWriteCallLogPermission
    val callLogs = callLogViewModel.callLogEntries
    val isLoading = callLogViewModel.isLoading
    val errorMessage = callLogViewModel.errorOccurred

    val permissionsToRequest = arrayOf(
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.WRITE_CALL_LOG
    )

    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val readGranted = permissions[Manifest.permission.READ_CALL_LOG] ?: hasReadPermission
            val writeGranted = permissions[Manifest.permission.WRITE_CALL_LOG] ?: hasWritePermission
            callLogViewModel.updatePermissionStatus(readGranted, writeGranted)

            if (readGranted) {
                Log.d("PermissionCheck", "Read Call Log permission granted by user.")
            } else {
                Log.d("PermissionCheck", "Read Call Log permission denied by user.")
                // TODO: Show a snackbar or dialog explaining why it's needed if denied permanently
            }
            // Similar logging for write permission
        }
    )

    // Effect to request permissions if not already granted when the screen is composed
    LaunchedEffect(key1 = Unit) { // Use Unit to run only once on initial composition
        if (!hasReadPermission || !hasWritePermission) {
            // It's often better to show a rationale before the first launch,
            // but for simplicity, we launch directly here.
            // Consider a state where user explicitly clicks a button after seeing rationale.
            multiplePermissionsLauncher.launch(permissionsToRequest)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_call_history_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!hasReadPermission) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("This app needs access to your call logs to display them.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        multiplePermissionsLauncher.launch(permissionsToRequest)
                    }) {
                        Text("Grant Permissions")
                    }
                }
            } else {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (errorMessage != null) {
                    Text(
                        text = "Error: $errorMessage",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(onClick = { callLogViewModel.fetchCallLogs() }) {
                        Text("Retry")
                    }
                } else if (callLogs.isEmpty()) {
                    Text("No call logs found.", modifier = Modifier.align(Alignment.CenterHorizontally))
                    Button(onClick = { callLogViewModel.fetchCallLogs() }) { // Allow manual refresh
                        Text("Refresh Call Logs")
                    }
                } else {
                    CallLogList(logs = callLogs)
                }
            }
        }
    }
}

@Composable
fun CallLogList(logs: List<CallLogEntry>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(logs, key = { log -> log.id }) { log ->
            CallLogItemView(log = log)
            HorizontalDivider()
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun EditCallHistoryScreenPermissionNotGrantedPreview() {
    CallEditorTheme {
        val mockViewModel = CallLogViewModel(Application()) // Requires Application instance
        EditCallHistoryScreen(navHostController = rememberNavController(), callLogViewModel = mockViewModel)
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun EditCallHistoryScreenWithLogsPreview() {
    CallEditorTheme {
        CallLogList(logs = listOf(
            CallLogEntry("1", "123-456-7890", Date().time - 100000, CallLog.Calls.INCOMING_TYPE, 60, "John Doe"),
            CallLogEntry("2", "987-654-3210", Date().time - 200000, CallLog.Calls.OUTGOING_TYPE, 120, "Jane Smith"),
            CallLogEntry("3", "555-555-5555", Date().time - 300000, CallLog.Calls.MISSED_TYPE, 0, null)
        ))
    }
}

@Preview(showBackground = true)
@Composable
fun CallLogItemViewPreview() {
    CallEditorTheme {
        CallLogItemView(
            log = CallLogEntry(
                id = "1",
                number = "123-456-7890",
                date = System.currentTimeMillis() - (2 * 60 * 60 * 1000), // 2 hours ago
                type = CallLog.Calls.INCOMING_TYPE,
                duration = 75,
                name = "John Doe"
            )
        )
    }
}

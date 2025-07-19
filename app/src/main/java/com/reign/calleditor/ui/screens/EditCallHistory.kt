package com.reign.calleditor.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.provider.CallLog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.reign.calleditor.R
import com.reign.calleditor.model.CallLogEntry
import com.reign.calleditor.ui.theme.CallEditorTheme
import com.reign.calleditor.ui.widgets.CallLogItemView
import com.reign.calleditor.ui.widgets.EmptyState
import com.reign.calleditor.ui.widgets.ErrorState
import com.reign.calleditor.ui.widgets.PermissionRationale
import com.reign.calleditor.viewmodel.CallLogViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCallHistoryScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    callLogViewModel: CallLogViewModel = viewModel()
) {
    val context = LocalContext.current

    val hasReadPermission = callLogViewModel.hasReadCallLogPermission
    val hasWritePermission = callLogViewModel.hasWriteCallLogPermission
    val callLogs = callLogViewModel.callLogEntries
    val isLoading = callLogViewModel.isLoading
    val errorMessage = callLogViewModel.errorOccurred

    val permissionsToRequest = remember {
        arrayOf(
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG
        )
    }

    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissionsResult ->
            val readGranted = permissionsResult[Manifest.permission.READ_CALL_LOG] ?: hasReadPermission
            val writeGranted = permissionsResult[Manifest.permission.WRITE_CALL_LOG] ?: callLogViewModel.hasWriteCallLogPermission // Use current value if not in map
            callLogViewModel.updatePermissionStatus(readGranted, writeGranted)
        }
    )

    LaunchedEffect(key1 = hasReadPermission) {
        if (!hasReadPermission) {
            // Consider showing a rationale before launching if it's not the first time.
            // For now, launch directly if permission is not granted.
            multiplePermissionsLauncher.launch(permissionsToRequest)
        }
        // No need to explicitly call fetchCallLogs here if ViewModel's init or updatePermissionStatus handles it.
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.edit_call_history_title)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                actions = {
                    IconButton(onClick = { /* TODO: Handle settings navigation or action */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box( // Use Box to easily center content or overlay loading indicators
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                !hasReadPermission && !isLoading -> { // Show permission request UI only if not loading initial check
                    PermissionRationale(
                        onRequestPermission = { multiplePermissionsLauncher.launch(permissionsToRequest) }
                    )
                }
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage != null -> {
                    ErrorState(
                        message = errorMessage ?: "An unknown error occurred.",
                        onRetry = { callLogViewModel.fetchCallLogs() }
                    )
                }
                callLogs.isEmpty() -> {
                    EmptyState(
                        message = "No call logs found.",
                        onRefresh = { callLogViewModel.fetchCallLogs() }
                    )
                }
                else -> {
                    CallLogList(logs = callLogs)
                }
            }
        }
    }
}

@Composable
fun CallLogList(logs: List<CallLogEntry>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(logs, key = { log -> log.id }) { log ->
            CallLogItemView(log = log, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
        }
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

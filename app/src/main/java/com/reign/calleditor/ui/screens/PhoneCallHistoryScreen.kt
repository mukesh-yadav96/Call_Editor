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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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
fun PhoneCallHistoryScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: CallLogViewModel
) {
    val context = LocalContext.current

    val hasReadPermission by remember { derivedStateOf { viewModel.hasReadCallLogPermission } }
    val hasWritePermission by remember { derivedStateOf { viewModel.hasWriteCallLogPermission } }
    val callLogs by remember { derivedStateOf { viewModel.callLogEntries } }
    val isLoading by remember { derivedStateOf { viewModel.isLoading } }
    val errorMessage by remember { derivedStateOf { viewModel.errorOccurred } }
    val setCurrentEntry = viewModel::setCurrentEntrySelected

    val permissionsToRequest = remember {
        arrayOf(
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val readGranted = permissions[Manifest.permission.READ_CALL_LOG] ?: hasReadPermission
        val writeGranted = permissions[Manifest.permission.WRITE_CALL_LOG] ?: hasWritePermission
        viewModel.updatePermissionStatus(readGranted, writeGranted)
    }

    LaunchedEffect(hasReadPermission, hasWritePermission) {
        if (!hasReadPermission || !hasWritePermission) {
            permissionLauncher.launch(permissionsToRequest)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.phone_call_logs)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { /* TODO: Navigate to settings */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                !hasReadPermission -> {
                    PermissionRationale(
                        onRequestPermission = { permissionLauncher.launch(permissionsToRequest) }
                    )
                }

                isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                errorMessage != null -> {
                    ErrorState(
                        message = errorMessage ?: "An unknown error occurred.",
                        onRetry = { viewModel.fetchCallLogs() }
                    )
                }

                callLogs.isEmpty() -> {
                    EmptyState(
                        message = "No call logs found.",
                        onRefresh = { viewModel.fetchCallLogs() }
                    )
                }

                else -> {
                    CallLogList(
                        navController = navController,
                        logs = callLogs,
                        setCurrentEntry = setCurrentEntry
                    )
                }
            }
        }
    }
}


@Composable
fun CallLogList(navController: NavHostController, logs: List<CallLogEntry>, setCurrentEntry: (CallLogEntry?) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(logs, key = { log -> log.id }) { log ->
            CallLogItemView(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                navController = navController,
                log = log,
                setCurrentEntry = setCurrentEntry
            )
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun EditCallHistoryScreenWithLogsPreview() {
    val navController = rememberNavController()
    CallEditorTheme {
        CallLogList(
            navController = navController,
            logs = listOf(
                CallLogEntry(
                    "1",
                    "123-456-7890",
                    Date().time - 100000,
                    CallLog.Calls.INCOMING_TYPE,
                    60,
                    "John Doe"
                ),
                CallLogEntry(
                    "2",
                    "987-654-3210",
                    Date().time - 200000,
                    CallLog.Calls.OUTGOING_TYPE,
                    120,
                    "Jane Smith"
                ),
                CallLogEntry(
                    "3",
                    "555-555-5555",
                    Date().time - 300000,
                    CallLog.Calls.MISSED_TYPE,
                    0,
                    null
                )
            ),
            setCurrentEntry = {}
        )
    }
}

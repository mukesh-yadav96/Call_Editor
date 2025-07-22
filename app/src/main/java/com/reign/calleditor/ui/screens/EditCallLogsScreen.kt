package com.reign.calleditor.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.reign.calleditor.ui.util.rememberEditCallLogState
import com.reign.calleditor.ui.widgets.EditCallLogForm
import com.reign.calleditor.ui.widgets.EditCallLogTopBar
import com.reign.calleditor.viewmodel.CallLogViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditCallLogsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: CallLogViewModel
) {
    val context = LocalContext.current
    val currentEntry = viewModel.getCurrentlySelectedEntry()
    val isLoading = viewModel.isLoading

    val uiState = rememberEditCallLogState(currentEntry)

    Scaffold(
        modifier = modifier,
        topBar = { EditCallLogTopBar { navController.popBackStack() } }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                EditCallLogForm(
                    state = uiState,
                    onSubmit = {
                        viewModel.updateCallLog(
                            id = currentEntry?.id ?: return@EditCallLogForm,
                            name = uiState.name,
                            number = uiState.number,
                            dateString = uiState.date,
                            timeString = uiState.time,
                            duration = uiState.durationInSeconds,
                            type = uiState.callType,
                            callback = { navController.popBackStack() }
                        )
                    }
                )
            }
        }
    }
}

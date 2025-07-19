package com.reign.calleditor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.reign.calleditor.viewmodel.CallLogViewModel

@Composable
fun EditCallLogsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: CallLogViewModel
) {
    val context = LocalContext.current
    val currentEntry = viewModel.getCurrentlySelectedEntry()

    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Screen ID: EditCallLogsScreen")
                Text("Editing Log with ID: ${currentEntry?.id}")

                if (currentEntry != null) {
                    Text("Name: ${currentEntry.name ?: "N/A"}")
                    Text("Number: ${currentEntry.number ?: "N/A"}")
                    // Add more details from currentEntry as needed
                } else {
                    Text("Current Entry is null.")
                }
            }
        }
    }
}
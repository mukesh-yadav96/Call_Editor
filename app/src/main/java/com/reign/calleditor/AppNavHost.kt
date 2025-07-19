package com.reign.calleditor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.reign.calleditor.routes.NavigationItem
import com.reign.calleditor.ui.screens.EditCallLogsScreen
import com.reign.calleditor.ui.screens.PhoneCallHistoryScreen
import com.reign.calleditor.viewmodel.CallLogViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = NavigationItem.PhoneCallHistory.baseRoute,
    callLogViewModel: CallLogViewModel = viewModel()
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            route = NavigationItem.PhoneCallHistory.baseRoute
        ) {
            PhoneCallHistoryScreen(
                navController = navController,
                callLogViewModel = callLogViewModel
            )
        }

        composable(
            route = NavigationItem.EditCallHistory.baseRoute
        ) {
            EditCallLogsScreen(
                navController = navController,
                viewModel = callLogViewModel
            )
        }
    }
}
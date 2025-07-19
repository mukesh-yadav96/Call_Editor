package com.reign.calleditor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.reign.calleditor.routes.NavigationItem
import com.reign.calleditor.ui.screens.EditCallHistoryScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = NavigationItem.EditCallHistory.route
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.EditCallHistory.route) {
            EditCallHistoryScreen(navController)
        }
    }
}
package com.reign.calleditor.routes

enum class Screen {
    EDIT_CALL_HISTORY,
}
sealed class NavigationItem(val route: String) {
    data object EditCallHistory : NavigationItem(Screen.EDIT_CALL_HISTORY.name)
}
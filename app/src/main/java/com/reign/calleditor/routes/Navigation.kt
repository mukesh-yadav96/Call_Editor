package com.reign.calleditor.routes

enum class Screen {
    PHONE_CALL_HISTORY,
    EDIT_CALL_HISTORY
}

sealed class NavigationItem(
    val baseRoute: String
) {
    data object PhoneCallHistory : NavigationItem(baseRoute = Screen.PHONE_CALL_HISTORY.name)
    data object EditCallHistory : NavigationItem(baseRoute = Screen.EDIT_CALL_HISTORY.name)
}
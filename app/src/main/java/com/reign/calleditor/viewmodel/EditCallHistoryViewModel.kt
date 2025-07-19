package com.reign.calleditor.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CallLog
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.database.getStringOrNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.reign.calleditor.model.CallLogEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CallLogViewModel(application: Application) : AndroidViewModel(application) {

    var hasReadCallLogPermission by mutableStateOf(checkInitialPermission(Manifest.permission.READ_CALL_LOG))
        private set
    var hasWriteCallLogPermission by mutableStateOf(checkInitialPermission(Manifest.permission.WRITE_CALL_LOG))
        private set

    var callLogEntries by mutableStateOf<List<CallLogEntry>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorOccurred by mutableStateOf<String?>(null)
        private set

    init {
        if (hasReadCallLogPermission) {
            fetchCallLogs()
        }
    }

    private fun checkInitialPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            getApplication<Application>().applicationContext,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun updatePermissionStatus(readGranted: Boolean, writeGranted: Boolean) {
        val oldReadPermission = hasReadCallLogPermission
        hasReadCallLogPermission = readGranted
        hasWriteCallLogPermission = writeGranted

        if (readGranted && (!oldReadPermission || callLogEntries.isEmpty() || errorOccurred != null)) {
            fetchCallLogs()
        } else if (!readGranted) {
            callLogEntries = emptyList()
            errorOccurred = "Read Call Log permission is required."
        }
    }

    fun fetchCallLogs() {
        if (!hasReadCallLogPermission) {
            Log.w("CallLogViewModel", "Read Call Log permission not granted. Cannot fetch logs.")
            errorOccurred = "Read Call Log permission is required to display call history."
            callLogEntries = emptyList() // Ensure logs are cleared if permission is missing
            isLoading = false // Ensure loading state is reset
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorOccurred = null
            try {
                val numberOfLogsToFetch = 50
                val logs = fetchRecentCallLogsFromProvider(
                    getApplication<Application>().applicationContext,
                    numberOfLogsToFetch
                )
                callLogEntries = logs
            } catch (e: Exception) {
                Log.e("CallLogViewModel", "Error fetching call logs", e)
                errorOccurred = "Failed to load call logs: ${e.message}"
                callLogEntries = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun fetchRecentCallLogsFromProvider(
        context: Context,
        limit: Int
    ): List<CallLogEntry> = withContext(Dispatchers.IO) {
        val logs = mutableListOf<CallLogEntry>()
        val contentResolver = context.contentResolver
        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.DATE,
            CallLog.Calls.TYPE,
            CallLog.Calls.DURATION,
            CallLog.Calls.CACHED_NAME
        )

        val sortOrder = "${CallLog.Calls.DATE} DESC" // Separate sort order
        val uriWithLimit = CallLog.Calls.CONTENT_URI.buildUpon()
            .appendQueryParameter("limit", limit.toString())
            .build()

        Log.d("CallLogViewModel", "Querying URI: $uriWithLimit with sortOrder: $sortOrder")

        val cursor = contentResolver.query(
            uriWithLimit,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(CallLog.Calls._ID)
            val numberColumn = it.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
            val dateColumn = it.getColumnIndexOrThrow(CallLog.Calls.DATE)
            val typeColumn = it.getColumnIndexOrThrow(CallLog.Calls.TYPE)
            val durationColumn = it.getColumnIndexOrThrow(CallLog.Calls.DURATION)
            val nameColumn = it.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)

            while (it.moveToNext()) {
                logs.add(
                    CallLogEntry(
                        id = it.getString(idColumn),
                        number = it.getStringOrNull(numberColumn),
                        date = it.getLong(dateColumn),
                        type = it.getInt(typeColumn),
                        duration = it.getLong(durationColumn),
                        name = it.getStringOrNull(nameColumn)
                    )
                )
            }
        } ?: run {
            Log.e("CallLogViewModel", "Cursor was null when querying call logs.")
        }
        Log.d("CallLogViewModel", "Fetched ${logs.size} call log entries from provider.")
        return@withContext logs
    }
}

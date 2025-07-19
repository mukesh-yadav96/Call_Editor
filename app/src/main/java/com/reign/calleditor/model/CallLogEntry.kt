package com.reign.calleditor.model

data class CallLogEntry(
    val id: String,
    val number: String?,
    val date: Long,
    val type: Int,
    val duration: Long,
    val name: String?
)


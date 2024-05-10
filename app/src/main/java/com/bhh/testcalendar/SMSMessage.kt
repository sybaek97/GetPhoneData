package com.bhh.testcalendar

data class SMSMessage(
    val address: String?,
    val threadId: String?,
    val date: String?,
    val msg: String?,
    val type: String?,
    val dateSent: String?,
    val read: Int,
    val seen: Int,
    val serviceCenter: String?,
    val subject: String?,
    val status: Int,
    val errorCode: Int,
    val id: Long
)
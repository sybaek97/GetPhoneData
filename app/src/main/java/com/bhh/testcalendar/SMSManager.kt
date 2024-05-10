package com.bhh.testcalendar

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CallLog
import android.util.Log
import java.time.LocalDate
import java.time.ZoneOffset

enum class SMSManager {
    INSTANCE;

    private val BATCH_SIZE = 15
    private val TAG = SMSManager::class.java.name

    fun getCallLogs(context: Context): List<UserCallLog> {
        val arrayList = ArrayList<UserCallLog>()
        val now = LocalDate.now()
        val oneMonthAgo = now.minusMonths(1)
//        var lastCallIdQueried = AndroidDataUtils.getLastCallIdQueried(context)
        val query: Cursor? = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            arrayOf("_id", "number", "type", CallLog.Calls.DATE, "duration", "name", "new", "numberlabel", "numbertype"),
            "date > ?",
            arrayOf(oneMonthAgo.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli().toString()),
            "_id DESC"
        )
        query?.use {
            while (query.moveToNext()) {
                val userCallLog = UserCallLog()
                val type = query.getInt(2)
                val id = query.getString(0)
                val number = query.getString(1)
                val date = query.getString(3)
                val duration = query.getString(4)
                val name = query.getString(5)
                val new = query.getInt(6)
                val numberLabel = query.getString(7)
                val numberType = query.getInt(8)

                userCallLog.id = id
                userCallLog.number = number
                userCallLog.type = type.toString()
                userCallLog.date = date
                userCallLog.duration = duration.toInt()
                userCallLog.name = name
                arrayList.add(userCallLog)
            }
        }
//        AndroidDataUtils.saveLastCallIdQueried(context, lastCallIdQueried)
        return arrayList
    }

    fun getSMSMessages(context: Context, callback: BatchedQueryCallback) {

        val now = LocalDate.now()
        val oneMonthAgo = now.minusDays(1)
        val arrayList = arrayListOf(
            "content://sms/inbox",
            "content://sms/sent"
        )
        for (str in arrayList) {

            val query: Cursor? = context.contentResolver.query(
                Uri.parse(str),
                arrayOf(
                    "_id",
                    "thread_id",
                    "address",
                    CallLog.Calls.DATE,
                    "body",
                    "type",
                    "person",
                    "date_sent",
                    "read",
                    "seen",
                    "service_center",
                    "subject",
                    "status",
                    "error_code"
                ),
                "date > ?",
                arrayOf(now.minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli().toString()),
                "_id DESC"
            )
            query?.use {
                Log.d(TAG,query.count.toString())
                if (query.count > 0) {
                    val smsMessages = ArrayList<SMSMessage>()
                    var count = 0
                    while (query.moveToNext()) {
                        val id = query.getLong(query.getColumnIndexOrThrow("_id"))

                        smsMessages.add(
                            SMSMessage(
                                query.getString(query.getColumnIndexOrThrow("address")),
                                query.getString(query.getColumnIndexOrThrow("thread_id")),
                                query.getString(query.getColumnIndexOrThrow("date")),
                                query.getString(query.getColumnIndexOrThrow("body")),
                                smsTypeToString(query.getString(query.getColumnIndexOrThrow("type"))),
                                query.getString(query.getColumnIndexOrThrow("date_sent")),
                                query.getInt(query.getColumnIndexOrThrow("read")),
                                query.getInt(query.getColumnIndexOrThrow("seen")),
                                query.getString(query.getColumnIndexOrThrow("service_center")),
                                query.getString(query.getColumnIndexOrThrow("subject")),
                                query.getInt(query.getColumnIndexOrThrow("status")),
                                query.getInt(query.getColumnIndexOrThrow("error_code")),
                                id
                            )
                        )
                        count++
                        if (count >= BATCH_SIZE) {
                            callback.onBatchQueried(smsMessages)
                            smsMessages.clear()
                            count = 0
                        }
                    }
                    if (count > 0) {
                        callback.onBatchQueried(smsMessages)
                    } else {

                    }
                } else {
                    Log.i(TAG, "No new SMS")
                }
            }
        }
    }

    private fun smsTypeToString(str: String): String {
        return when (str.toInt()) {
            0 -> "all"
            1 -> "inbox"
            2 -> "sent"
            3 -> "draft"
            4 -> "outbox"
            5 -> "failed"
            6 -> "queued"
            else -> "unknown"
        }
    }

    companion object {
        private val BATCH_SIZE = 15
        private val TAG = SMSManager::class.java.name
    }
}
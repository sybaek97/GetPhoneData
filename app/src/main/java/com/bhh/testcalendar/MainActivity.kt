package com.bhh.testcalendar

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val permissions = arrayOf(
        android.Manifest.permission.READ_CALENDAR,
        android.Manifest.permission.WRITE_CALENDAR,
        android.Manifest.permission.READ_SMS,
        android.Manifest.permission.READ_CALL_LOG

    )

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            Log.d("Permissions", "All permissions granted")
            loadCalendarEvents()
        } else {
            Log.d("Permissions", "Not all permissions granted")
            // Handle the case where permissions are not granted
        }
    }
    private fun allPermissionsGranted() = permissions.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissionsAndLoad() {
        if (allPermissionsGranted()) {
            loadCalendarEvents()
        } else {
            requestPermissionLauncher.launch(permissions)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build()
        )

        checkPermissionsAndLoad()
//        loadCalendarEvents()
    }

    private fun loadCalendarEvents() {
        Thread {
//            val events = CalendarEventsManager.readCalendarEvent(this)
//            if (events.isNotEmpty()) {
//                events.forEach { event ->
//                    Log.d("CalendarEvent", buildEventDetails(event))
//                }
//            } else {
//                Log.d("CalendarEvent", "No events found")
//            }
//            val callLogs = SMSManager.INSTANCE.getCallLogs(this)
//            if (callLogs.isNotEmpty()) {
//                callLogs.forEach { callLog ->
//                    Log.d("CallLog", callLog.toString())
//                }
//            } else {
//                Log.d("CallLog", "No call logs found")
//            }
//
            val smsMessages = ArrayList<SMSMessage>()
            SMSManager.INSTANCE.getSMSMessages(this, object : BatchedQueryCallback {


                override fun onBatchQueried(list: ArrayList<SMSMessage>) {
                    smsMessages.addAll(list)
                }
            })

            if (smsMessages.isNotEmpty()) {
                smsMessages.forEach { smsMessage ->
                    Log.d("SMSMessage", smsMessage.toString())
                }
            } else {
                Log.d("SMSMessage", "No SMS messages found")
            }
        }.start()
    }

    private fun buildEventDetails(event: CalendarEvent): String {

        return with(event) {
            """
            ID: $id

             Title: $title
                         Calendar ID: ${calendarDetails?.id}
                         Calendar Name: ${calendarDetails?.name}
             Description: $description
                Start Date: $startDate
                  End Date: $endDate
                         Location: $location
                          All Day: ${if (allDay == 1) "Yes" else "No"}
                          Organizer: $organizer
                                 Timezone: $timezone
                         End Timezone: $endTimezone
                         Access Level: ${getAccessLevel(accessLevel)}
                           Availability: ${getAvailability(availability)}
                               Event Color: $eventColor
                                Exception Dates: $exDate
                         Exception Rule: $exRule
                           Guests Can Invite Others: ${guestsCanInviteOthers == 1}
                             Guests Can Modify: ${guestsCanModify == 1}
                         Guests Can See Guests: ${guestsCanSeeGuests == 1}
                         Has Alarm: ${hasAlarm == 1}
                         Has Extended Properties: ${hasExtendedProperties == 1}
                           Recurrence Date: $rDate
                         Recurrence Rule: $rRule
                         
                        Attendees: ${attendees.joinToString { "${it.name} (${it.email})" }}
            instances: ${eventInstances.joinToString { "id: ${it.id} begin:${it.begin} end :${it.end} " }}
            """.trimIndent()
        }
    }

    private fun getAccessLevel(level: Int): String = when (level) {
        0 -> "None"
        1 -> "Confidential"
        2 -> "Private"
        3 -> "Public"
        else -> "Unknown"
    }

    private fun getAvailability(availability: Int): String = when (availability) {
        0 -> "Busy"
        1 -> "Free"
        2 -> "Tentative"
        3 -> "Unavailable"
        else -> "Unknown"
    }
}




//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.provider.CalendarContract
//import android.util.Log
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import com.bhh.testcalendar.R
//import java.text.SimpleDateFormat
//import java.util.Date
//
//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)
//        } else {
//            loadCalendarEvents()
//        }
//    }
//
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            loadCalendarEvents()
//        } else {
//            Log.e("CalendarPermission", "Permission denied by user.")
//        }
//    }
//
//    private fun loadCalendarEvents() {
//        val uri = CalendarContract.Events.CONTENT_URI
//        val projection = arrayOf(
//            CalendarContract.Events._ID,
//            CalendarContract.Events.TITLE,
//            CalendarContract.Events.DTSTART,
//            CalendarContract.Events.DTEND
//        )
//
//        val cursor = contentResolver.query(uri, projection, null, null, null)
//        cursor?.use {
//            while (it.moveToNext()) {
//                val id = it.getLong(0)
//                val title = it.getString(1)
//                val startDate = Date(it.getLong(2))
//                val endDate = Date(it.getLong(3))
//
//                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//                Log.d("CalendarEvent", "ID: $id, Title: $title, Start: ${formatter.format(startDate)}, End: ${formatter.format(endDate)}")
//            }
//        }
//    }
//}

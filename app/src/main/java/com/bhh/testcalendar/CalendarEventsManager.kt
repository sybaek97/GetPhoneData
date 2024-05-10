package com.bhh.testcalendar

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import java.util.Calendar
import java.util.Date

object CalendarEventsManager {
    private val TAG = CalendarEventsManager::class.java.simpleName

    private fun getCalendar(context: Context, calendarId: Long): CalendarDetails? {
        val uri = CalendarContract.Calendars.CONTENT_URI
        val selection = "_id = ?"
        val selectionArgs = arrayOf(calendarId.toString())

        context.contentResolver.query(
            uri,
            arrayOf("_id", "calendar_displayName"),
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                return CalendarDetails(
                    id = cursor.getLong(0),
                    name = cursor.getString(1)
                )
            }
        }
        return null
    }

    private fun getEventInstances(context: Context, eventId: Long): List<EventInstances> {

        val uri = CalendarContract.Instances.CONTENT_URI.buildUpon().apply {
            ContentUris.appendId(this, Long.MIN_VALUE)
            ContentUris.appendId(this, Long.MAX_VALUE)
        }.build()
//        Log.d(TAG,uri.toString())
        val selection =
            "event_id = ? AND begin > ${getDateMonthsBeforeNow()} AND end < ${getDateMonthsAfterNow()}"
//        Log.d(TAG,selection.toString())
        val selectionArgs = arrayOf(eventId.toString())

        return context.contentResolver.query(
            uri,
            arrayOf("_id", "begin", "end", "endDay", "endMinute", "startDay", "startMinute"),
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            generateSequence { if (cursor.moveToNext()) cursor else null }
                .map {
//                    Log.d(TAG,it.toString()+" getEventInstances")
                    EventInstances(
                        id = it.getLong(0),
                        begin = it.getLong(1),
                        end = it.getLong(2),
                        endDay = it.getLong(3),
                        endMinute = it.getLong(4),
                        startDay = it.getInt(5),
                        startMinute = it.getInt(6)
                    )
                }.toList()
        } ?: emptyList()
    }

    fun readCalendarEvent(context: Context): List<CalendarEvent> {

        val uri = CalendarContract.Events.CONTENT_URI
        val selection =
            "dtstart > ${getDateMonthsBeforeNow()} AND dtstart < ${getDateMonthsAfterNow()}"


            return context.contentResolver.query(
                uri,
                arrayOf(
                    "_id",
                    "title",
                    "description",
                    "dtstart",
                    "dtend",
                    "eventLocation",
                    "allDay",
                    "calendar_id",
                    "hasAttendeeData",
                    "organizer",
                    "eventTimezone",
                    "eventEndTimezone",
                    "accessLevel",
                    "availability",
                    "eventColor",
                    "exdate",
                    "exrule",
                    "guestsCanInviteOthers",
                    "guestsCanModify",
                    "guestsCanSeeGuests",
                    "hasAlarm",
                    "hasExtendedProperties",
                    "rdate",
                    "rrule"
                ),
                selection,
                null,
                null
            )?.use { cursor ->
                generateSequence { if (cursor.moveToNext()) cursor else null }
                    .map {

                        CalendarEvent(
                            id = it.getLong(0),
                            title = it.getString(1),
                            description = it.getString(2),
                            startDate = Date(it.getLong(3)),
                            endDate = Date(it.getLong(4)),
                            location = it.getString(5),
                            allDay = it.getInt(6),
                            calendarDetails = getCalendar(context, it.getLong(6)),
                            attendees = if (it.getInt(7) > 0) getAttendee(
                                context,
                                it.getLong(0)
                            ) else emptyList(),
                            organizer = it.getString(9),
                            timezone = it.getString(10),
                            endTimezone = it.getString(11),
                            accessLevel = it.getInt(12),
                            availability = it.getInt(13),
                            eventColor = it.getInt(14),
                            exDate = it.getString(15),
                            exRule = it.getString(16),
                            guestsCanInviteOthers = it.getInt(17),
                            guestsCanModify = it.getInt(18),
                            guestsCanSeeGuests = it.getInt(19),
                            hasAlarm = it.getInt(20),
                            hasExtendedProperties = it.getInt(21),
                            rDate = it.getString(22),
                            rRule = it.getString(23),
                            eventInstances = getEventInstances(context, it.getLong(0))
                        )
                    }.toList()
            } ?: emptyList()


    }

    private fun getAttendee(context: Context, eventId: Long): List<AndroidAttendee> {
        val uri = CalendarContract.Attendees.CONTENT_URI
        val selection = "event_id = ?"
        val selectionArgs = arrayOf(eventId.toString())

        return context.contentResolver.query(
            uri,
            arrayOf(
                "_id",
                "attendeeEmail",
                "attendeeName",
                "attendeeStatus",
                "attendeeType",
                "attendeeRelationship"
            ),
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            generateSequence { if (cursor.moveToNext()) cursor else null }
//                .filter { it.getString(1).isNotBlank() }
                .map {
//                    Log.d(TAG,it.getString(1)+" getAttendee")

//                    Log.d(TAG,getRelationshipString(it.getInt(5)))
                    AndroidAttendee(
                        id = it.getLong(0),
                        email = it.getString(1),
                        name = it.getString(2),
                        status = getStatusString(it.getInt(3)),
                        type = getTypeString(it.getInt(4)),
                        relationship = getRelationshipString(it.getInt(5))
                    )
                }.toList()
        } ?: emptyList()
    }

    private fun getDateMonthsBeforeNow(): String {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MONTH, -1)
        }
        return calendar.timeInMillis.toString()
    }

    private fun getDateMonthsAfterNow(): String {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MONTH, 7)
        }
        return calendar.timeInMillis.toString()
    }

    private fun getStatusString(status: Int): String {
        return when (status) {
            1 -> "accepted"
            2 -> "declined"
            3 -> "invited"
            4 -> "tentative"
            else -> "others"
        }
    }

    private fun getTypeString(type: Int): String {
        return when (type) {
            1 -> "required"
            2 -> "optional"
            3 -> "resource"
            else -> "others"
        }
    }

    private fun getRelationshipString(relationship: Int): String {
        return when (relationship) {
            1 -> "attendee"
            2 -> "organizer"
            3 -> "performer"
            4 -> "speaker"
            else -> "others"
        }
    }
}
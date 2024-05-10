package com.bhh.testcalendar

import java.util.Date

data class CalendarEvent(
    var title: String? = null,
    var begin: Date? = null,
    var end: Date? = null,
    var allDay: Int = 0,
    var description: String? = null,
    var startDate: Date? = null,
    var endDate: Date? = null,
    var location: String? = null,
    var id: Long = 0L,
    var calendarDetails: CalendarDetails? = null,
    var organizer: String? = null,
    var timezone: String? = null,
    var endTimezone: String? = null,
    var accessLevel: Int = 0,
    var availability: Int = 0,
    var eventColor: Int = 0,
    var exDate: String? = null,
    var exRule: String? = null,
    var guestsCanInviteOthers: Int = 0,
    var guestsCanModify: Int = 0,
    var guestsCanSeeGuests: Int = 0,
    var hasAlarm: Int = 0,
    var hasExtendedProperties: Int = 0,
    var rDate: String? = null,
    var rRule: String? = null,
    var attendees: List<AndroidAttendee> = arrayListOf(),
    var eventInstances: List<EventInstances> = arrayListOf()
) : Comparable<CalendarEvent> {
    override fun compareTo(other: CalendarEvent): Int {
        return this.begin?.compareTo(other.begin ?: Date(0)) ?: -1
    }

    override fun toString(): String {
        return "$title $begin $end $allDay"
    }
}
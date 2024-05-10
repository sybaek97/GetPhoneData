package com.bhh.testcalendar

class AndroidAttendee(
    var email: String? = null,
    var id: Long = 0L,
    var name: String? = null,
    var relationship: String? = null,
    var status: String? = null,
    var type: String? = null,
    private var isOrganizer: Boolean = false
) {
    fun isOrganizer(): Boolean = isOrganizer

    fun setOrganizer(isOrganizer: Boolean) {
        this.isOrganizer = isOrganizer
    }
}
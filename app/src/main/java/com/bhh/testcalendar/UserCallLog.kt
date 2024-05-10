package com.bhh.testcalendar

class UserCallLog {
    companion object {
        const val CALL_TYPE_INCOMING_CALL = "incoming"
        const val CALL_TYPE_MISSED_CALL = "missed"
        const val CALL_TYPE_OUTGOING_CALL = "outgoing"
    }

    var id: String? = null
    var number: String? = null
    var type: String? = null
    var date: String? = null
    var duration: Int = 0
    var name: String? = null

    fun setType(type: Int) {
        this.type = when (type) {
            1 -> CALL_TYPE_INCOMING_CALL
            2 -> CALL_TYPE_OUTGOING_CALL
            3 -> CALL_TYPE_MISSED_CALL
            else -> ""
        }
    }
}
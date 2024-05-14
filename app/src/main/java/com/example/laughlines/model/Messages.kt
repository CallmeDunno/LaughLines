package com.example.laughlines.model

data class Messages(val message: String, val sender: String, val timestamp: Long, val type: Int) {
    companion object {
        class SortByTimestamp : Comparator<Messages> {
            override fun compare(m1: Messages, m2: Messages): Int {
                return m1.timestamp.compareTo(m2.timestamp)
            }
        }
    }
}



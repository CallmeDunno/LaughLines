package com.example.laughlines.model

data class Messages(
    val messageID: String?,
    val message: String,
    val recipient: String,
    val sender: String,
    val timestamp: String
) {
    companion object {
        class SortByDateTime : Comparator<Messages> {
            override fun compare(m1: Messages, m2: Messages): Int {
                return m1.timestamp.compareTo(m2.timestamp)
            }
        }
    }
}



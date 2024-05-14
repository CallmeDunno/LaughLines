package com.example.laughlines.model

data class Contact (val id: String, val chatId: String, val friendId: String, val account: Account, var lastTime: Long) {
    companion object {
        class SortByTimestamp : Comparator<Contact> {
            override fun compare(m1: Contact, m2: Contact): Int {
                return m2.lastTime.compareTo(m1.lastTime)
            }
        }
    }
}
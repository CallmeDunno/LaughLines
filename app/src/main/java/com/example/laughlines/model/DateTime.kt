package com.example.laughlines.model

class DateTime(private val dateTime: String) : Comparable<DateTime> {
    private val month: Int   // month (between 1 and 12)
    private val day: Int     // day   (between 1 and DAYS[month])
    private val year: Int    // year
    private val hour: Int
    private val minute: Int

    init {
        val dt = dateTime.split(" ")
        val time = dt[1]
        val date = dt[0]

        val fields = date.split("-")
        if (fields.size != 3) {
            throw IllegalArgumentException("Invalid date ${fields[0]}")
        }
        day = fields[0].toInt()
        month = fields[1].toInt()
        year = fields[2].toInt()
        if (!isValidDate(day, month, year)) throw IllegalArgumentException("Invalid date")

        val timeFields = time.split(":")
        if (timeFields.size != 2) {
            throw IllegalArgumentException("Invalid time")
        }
        hour = timeFields[0].toInt()
        minute = timeFields[1].toInt()
        if (!isValidTime(hour, minute)) {
            throw IllegalArgumentException("Invalid time")
        }
    }

    // is the given date valid?
    private fun isValidDate(d: Int, m: Int, y: Int): Boolean {
        if (m < 1 || m > 12) return false
        if (d < 1 || d > DAYS[m]) return false
        return m != 2 || d != 29 || isLeapYear(y)
    }

    private fun isValidTime(h: Int, m: Int): Boolean {
        if (h < 0 || h > 24) {
            return false
        }
        return m in 0..59
    }

    // is y a leap year?
    private fun isLeapYear(y: Int): Boolean {
        if (y % 400 == 0) return true
        if (y % 100 == 0) return false
        return y % 4 == 0
    }

    override fun compareTo(other: DateTime): Int {
        if (this.year < other.year) return -1
        if (this.year > other.year) return 1
        if (this.month < other.month) return -1
        if (this.month > other.month) return 1
        if (this.day < other.day) return -1
        if (this.day > other.day) return 1
        if (this.hour < other.hour) return -1
        if (this.hour > other.hour) return 1
        if (this.minute < other.minute) return -1
        if (this.minute > other.minute) return 1
        return 0
    }

    override fun toString(): String {
        return when {
            hour < 10 && minute < 10 -> "$day-$month-$year 0$hour:0$minute"
            hour < 10 -> "$day-$month-$year 0$hour:$minute"
            minute < 10 -> "$day-$month-$year $hour:0$minute"
            else -> "$day-$month-$year $hour:$minute"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as DateTime
        return month == that.month && day == that.day && year == that.year && hour == that.hour && minute == that.minute
    }

    override fun hashCode(): Int {
        var hash = 17
        hash = 31 * hash + month
        hash = 31 * hash + day
        hash = 31 * hash + year
        hash = 31 * hash + hour
        hash = 31 * hash + minute
        return hash
    }

    companion object {
        private val DAYS = intArrayOf(0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    }
}

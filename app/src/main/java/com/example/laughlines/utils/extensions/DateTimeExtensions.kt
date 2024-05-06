package com.example.laughlines.utils.extensions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun formatDateTime(type: String) : String {
    val formatter = SimpleDateFormat(type)
    return formatter.format(Date())
}
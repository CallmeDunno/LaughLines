package com.example.laughlines.utils.extensions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun formatDateTime() : String {
    val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm")
    return formatter.format(Date())
}
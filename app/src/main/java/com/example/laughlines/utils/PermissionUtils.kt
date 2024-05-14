package com.example.laughlines.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object PermissionUtils {
    fun checkPermission(permission: String?, context: Context?): Boolean {
        return if (context != null) {
            ActivityCompat.checkSelfPermission(context, permission!!) == PackageManager.PERMISSION_GRANTED
        } else false
    }

    fun checkPermissionList(context: Context?, listPermission: Array<String>): Boolean {
        for (per in listPermission) {
            val allow = ActivityCompat.checkSelfPermission(context!!, per) == PackageManager.PERMISSION_GRANTED
            if (!allow) return false
        }
        return true
    }



}
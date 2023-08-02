package com.example.laughlines.log

import android.util.Log

class Logger {
    companion object {
        fun d(msg: String) {
            Log.d("Dunno", msg)
        }
        fun e(msg: String) {
            Log.e("Dunno", msg)
        }
        fun w(msg: String) {
            Log.w("Dunno", msg)
        }
        fun i(msg: String) {
            Log.i("Dunno", msg)
        }
    }
}
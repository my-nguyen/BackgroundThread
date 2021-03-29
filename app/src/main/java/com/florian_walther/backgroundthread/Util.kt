package com.florian_walther.backgroundthread

import android.os.SystemClock
import android.util.Log

object Util {
    fun run(tag1: String, tag2: String="") {
        for (i in 1..5) {
            Log.d(tag1, "${tag2 ?: "run"} $i")
            // Log.d(tag, "run: $i")
            // same as sleep() without the try-catch block
            SystemClock.sleep(1000)
        }
    }
}
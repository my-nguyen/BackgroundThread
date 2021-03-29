package com.florian_walther.backgroundthread

import android.os.Handler
import android.os.HandlerThread
import android.os.Process

class ExampleHandlerThread: HandlerThread("ExampleHandlerThread", Process.THREAD_PRIORITY_BACKGROUND) {
    companion object {
        const val TAG = "ExampleHandlerThread"
    }

    private lateinit var handler: Handler
}
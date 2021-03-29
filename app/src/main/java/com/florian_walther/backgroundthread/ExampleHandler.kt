package com.florian_walther.backgroundthread

import android.os.Handler
import android.os.Message
import android.util.Log

class ExampleHandler: Handler() {
    companion object {
        const val TASK_A = 1
        const val TASK_B = 2
        private const val TAG = "ExampleHandler"
    }

    // this is where message arrives, to be executed
    override fun handleMessage(msg: Message) {
        when (msg.what) {
            TASK_A -> Log.d(TAG, "Task A executed")
            TASK_B -> Log.d(TAG, "Task B executed")
        }
    }
}
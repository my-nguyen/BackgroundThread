package com.florian_walther.backgroundthread

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.Process
import android.util.Log

class ExampleHandlerThread: HandlerThread("ExampleHandlerThread", Process.THREAD_PRIORITY_BACKGROUND) {
    companion object {
        const val TAG = "ExampleHandlerThread"
        const val EXAMPLE_TASK = 1
    }

    lateinit var handler: Handler

    // this method is called in after Looper.prepare() and before Looper.loop()
    override fun onLooperPrepared() {
        // prepare1()
        prepare2()
    }

    private fun prepare1() {
        handler = Handler()
    }

    private fun prepare2() {
        handler = object: Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    EXAMPLE_TASK -> {
                        Log.d(TAG, "Example Task, arg1: ${msg.arg1}, obj: ${msg.obj}")
                        Util.run(TAG, "handleMessage:")
                    }
                }
            }
        }
    }
}
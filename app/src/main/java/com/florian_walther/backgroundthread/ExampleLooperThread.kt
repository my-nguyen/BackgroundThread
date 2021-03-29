package com.florian_walther.backgroundthread

import android.os.Handler
import android.os.Looper
import android.util.Log

private const val TAG = "MyThread"

class ExampleLooperThread: Thread() {

    lateinit var handler: Handler
    lateinit var looper: Looper

    override fun run() {
        // add a Looper to this background thread and create a message queue
        Looper.prepare()

        // get the looper of the current thread
        looper = Looper.myLooper()!!

        // handler = Handler()
        handler = ExampleHandler()

        // run1()
        run2()

        Log.d(TAG, "end of run()")
    }

    // this way you can run the thread only once
    private fun run1() {
        Util.run(TAG)
    }

    // run in an infinite loop
    private fun run2() {
        Looper.loop()
    }
}
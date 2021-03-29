package com.florian_walther.backgroundthread

import android.os.Handler
import android.os.Looper
import android.util.Log

class ExampleLooperThread: Thread() {

    companion object {
        const val TAG = "MyThread"
    }

    lateinit var handler: Handler
    lateinit var looper: Looper

    override fun run() {
        // loop1()
        loop2()
    }

    private fun loop1() {
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

    private fun loop2() {
        Looper.prepare()
        handler = Handler()
        Looper.loop()

        Util.run(TAG)
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
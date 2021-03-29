package com.florian_walther.backgroundthread

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.florian_walther.backgroundthread.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    class MyThread(val seconds: Int): Thread() {
        override fun run() {
            super.run()
            // sleep for so many seconds
            for (i in 1..seconds) {
                Log.d(TAG, "startThread: $i")
                Thread.sleep(1000)
            }
        }
    }

    inner class MyRunnable(val seconds: Int): Runnable {
        override fun run() {
            // sleep for so many seconds
            for (i in 1..seconds) {
                if (threadStopped) {
                    return
                }

                if (i == 6) {
                    // 1. attempting to directly update the UI thread; this would crash
                    binding.btnStart.text = "50%"

                    // 2. instead we must use the handler from main thread to update the main thread
                    mainHandler.post {
                        binding.btnStart.text = "50%"
                    }

                    // 3. if we create a handler in this thread and update the main thread
                    // via this handler, it would also crash
                    val threadHandler1 = Handler()
                    threadHandler1.post {
                        binding.btnStart.text = "50%"
                    }

                    // 4. instead, if a handler is tied to the main thread as follows, it can
                    // update the main thread
                    val threadHandler2 = Handler(Looper.getMainLooper())
                    threadHandler2.post {
                        binding.btnStart.text = "50%"
                    }

                    // 5. alternatively the View class has a convenient post() method which allows
                    // update to the main thread
                    binding.btnStart.post {
                        binding.btnStart.text = "50%"
                    }

                    // 6. this will also work
                    runOnUiThread {
                        binding.btnStart.text = "50%"
                    }
                }
                Log.d(TAG, "startThread: $i")
                Thread.sleep(1000)
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    lateinit var binding: ActivityMainBinding
    lateinit var mainHandler: Handler

    @Volatile var threadStopped = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // handler is created on the main thread so it will only run on the main thread
        mainHandler = Handler()

        binding.btnStart.setOnClickListener {
            threadStopped = false
            // any of the calls below will work
            // startThread()
            startRunnable()
            // startAnonymous()
        }
        binding.btnStop.setOnClickListener {
            threadStopped = true
        }
    }

    private fun startThread() {
        val thread = MyThread(10)
        thread.start()
    }

    private fun startRunnable() {
        val runnable = MyRunnable(10)
        Thread(runnable).start()
    }

    private fun startAnonymous() {
        Thread {
            // do something equivalent to the MyRunnable.run() method
        }.start()
    }
}
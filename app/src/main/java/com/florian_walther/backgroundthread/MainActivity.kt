package com.florian_walther.backgroundthread

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.florian_walther.backgroundthread.ExampleHandler.Companion.TASK_A
import com.florian_walther.backgroundthread.ExampleHandler.Companion.TASK_B
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

    private val looperThread = ExampleLooperThread()

    @Volatile var threadStopped = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // handler is created on the main thread so it will only run on the main thread
        mainHandler = Handler()

        binding.btnStart.setOnClickListener {
            // start1()
            start2()
        }

        binding.btnStop.setOnClickListener {
            // stop1()
            stop2()
        }

        binding.btnTaskA.setOnClickListener {
            // send something from the UI thread to the message queue of MyThread
            /*// method #1 to run the looper
            looperThread.handler.post {
                Util.run(TAG)
            }*/

            /*// method #2 to run the looper
            // this starts an anonymous inner class which maintains a reference to the outer class
            // (MainActivity), so if MainActivity is destroyed, as when the screen is rotated, it's
            // not garbage collected, resulting in a memory leak.
            // a fix would be to create a Runnable class as a static inner class but you won't be
            // able to access the outer class (MainActivity)'s variables.
            // if you still need to access outer class' variables, then use a WeakReference
            val threadHandler = Handler(looperThread.looper)
            threadHandler.post {
                Util.run(TAG)
            }*/

            // method #3: in method #2, in a Runnable, a piece of work is sent to execute.
            // here a Message contains raw data (like the what field), which the Runnable doesn't
            // know how to handle
            val message = Message.obtain()
            message.what = TASK_A
            looperThread.handler.sendMessage(message)
        }

        binding.btnTaskB.setOnClickListener {
            val message = Message.obtain()
            message.what = TASK_B
            looperThread.handler.sendMessage(message)
        }
    }

    private fun start1() {
        // threadStopped = false
        // any of the calls below will work
        // startThread()
        // startRunnable()
        // startAnonymous()
    }

    private fun start2() {
        // after a thread finishes its run, clicking on Start will start another run
        looperThread.start()
    }

    private fun stop1() {
        threadStopped = true
    }

    private fun stop2() {
        looperThread.looper.quit()
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
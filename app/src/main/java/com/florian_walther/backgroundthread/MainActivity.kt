package com.florian_walther.backgroundthread

import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.florian_walther.backgroundthread.ExampleHandler.Companion.TASK_A
import com.florian_walther.backgroundthread.ExampleHandler.Companion.TASK_B
import com.florian_walther.backgroundthread.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    class MyThread(val seconds: Int) : Thread() {
        override fun run() {
            super.run()
            // sleep for so many seconds
            for (i in 1..seconds) {
                Log.d(TAG, "startThread: $i")
                Thread.sleep(1000)
            }
        }
    }

    inner class MyRunnable(val seconds: Int) : Runnable {
        override fun run() {
            // sleep for so many seconds
            for (i in 1..seconds) {
                if (threadStopped) {
                    return
                }

                if (i == 6) {
                    // 1. attempting to directly update the UI thread; this would crash
                    // update1a()

                    // 2. instead we must use the handler from main thread to update the main thread
                    // update1b()

                    // 3. if we create a handler in this thread and update the main thread
                    // via this handler, it would also crash
                    // update1c()

                    // 4. instead, if a handler is tied to the main thread as follows, it can
                    // update the main thread
                    // update1d()

                    // 5. alternatively the View class has a convenient post() method which allows
                    // update to the main thread
                    // update1e()

                    // 6. this will also work
                    update1f()
                }
                Log.d(TAG, "startThread: $i")
                Thread.sleep(1000)
            }
        }
    }

    // avoid memory leak by not maintaining a reference to the outer class (MainActivity)
    class ExampleRunnable1 : Runnable {
        override fun run() {
            Util.run(TAG, "Runnable1")
        }
    }

    class ExampleRunnable2 : Runnable {
        override fun run() {
            Util.run(TAG, "Runnable2")
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    lateinit var binding: ActivityMainBinding
    lateinit var mainHandler: Handler
    lateinit var threadHandler: Handler

    private val looperThread = ExampleLooperThread()
    private val handlerThread = HandlerThread("HandlerThread")
    private val runnable1 = ExampleRunnable1()
    private val token = Object()

    var exampleHandlerThread = ExampleHandlerThread()

    @Volatile
    var threadStopped = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // create1()
        // create2a()
        create2b()
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerThread.quit()
    }

    private fun create1() {
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
            // method #1 to run the looper
            // post1a()

            // method #2 to run the looper
            // post1b()

            // method #3: in method #2, in a Runnable, a piece of work is sent to execute.
            post1c()
        }

        binding.btnTaskB.setOnClickListener {
            val message = Message.obtain()
            message.what = TASK_B
            looperThread.handler.sendMessage(message)
        }
    }

    private fun create2a() {
        handlerThread.start()
        threadHandler = Handler(handlerThread.looper)

        // work2a()
        work2b()
    }

    private fun create2b() {
        exampleHandlerThread.start()
        work2c()
        // remove2a()
        remove2e()
    }

    private fun work2a() {
        binding.btnDoWork.setOnClickListener {
            threadHandler.postDelayed(ExampleRunnable1(), 2000)
            threadHandler.post(ExampleRunnable2())
        }
    }

    private fun work2b() {
        binding.btnDoWork.setOnClickListener {
            threadHandler.post(ExampleRunnable1())
            threadHandler.post(ExampleRunnable1())
            threadHandler.postAtFrontOfQueue(ExampleRunnable2())
        }
    }

    private fun work2c() {
        // message2a()
        // message2b()
        // message2c()
        // message2d()
        // message2e()
        message2f()
    }

    private fun remove2a() {
        binding.btnRemoveMessages.setOnClickListener {
            exampleHandlerThread.handler.removeCallbacksAndMessages(null)
        }
    }

    private fun remove2e() {
        binding.btnRemoveMessages.setOnClickListener {
            // remove specific Runnable instance
            exampleHandlerThread.handler.removeCallbacks(runnable1)
        }
    }

    private fun remove2f() {
        binding.btnRemoveMessages.setOnClickListener {
            // remove specific Runnable instance
            exampleHandlerThread.handler.removeCallbacks(runnable1, token)
        }
    }

    private fun message2a() {
        val message = Message.obtain()
        message.what = 1
        exampleHandlerThread.handler.sendMessage(message)

        binding.btnDoWork.setOnClickListener {
            exampleHandlerThread.handler.post(ExampleRunnable1())
            exampleHandlerThread.handler.post(ExampleRunnable1())
            exampleHandlerThread.handler.postAtFrontOfQueue(ExampleRunnable2())
        }
    }

    private fun message2b() {
        exampleHandlerThread.handler.sendEmptyMessage(1)

        binding.btnDoWork.setOnClickListener {
            exampleHandlerThread.handler.post(ExampleRunnable1())
            exampleHandlerThread.handler.post(ExampleRunnable1())
            exampleHandlerThread.handler.postAtFrontOfQueue(ExampleRunnable2())
        }
    }

    private fun message2c() {
        val message = Message.obtain()
        message.what = 1
        message.arg1 = 23
        message.obj = "Some string"

        exampleHandlerThread.handler.sendMessage(message)
    }

    private fun message2d() {
        val message = Message.obtain(exampleHandlerThread.handler)
        message.what = 1
        message.arg1 = 23
        message.obj = "Some string"
        message.sendToTarget()

        exampleHandlerThread.handler.sendEmptyMessage(1)
        exampleHandlerThread.handler.post(ExampleRunnable1())
    }

    private fun message2e() {
        val message = Message.obtain(exampleHandlerThread.handler)
        message.what = 1
        message.arg1 = 23
        message.obj = "Some string"
        message.sendToTarget()

        exampleHandlerThread.handler.post(ExampleRunnable1())
        exampleHandlerThread.handler.post(runnable1)
    }

    private fun message2f() {
        val message = Message.obtain(exampleHandlerThread.handler)
        message.what = 1
        message.arg1 = 23
        message.obj = "Some string"
        message.sendToTarget()

        exampleHandlerThread.handler.postAtTime(runnable1, token, SystemClock.uptimeMillis())
        exampleHandlerThread.handler.post(runnable1)
    }

    private fun update1a() {
        binding.btnStart.text = "50%"
    }

    private fun update1b() {
        mainHandler.post {
            binding.btnStart.text = "50%"
        }
    }

    private fun update1c() {
        val threadHandler1 = Handler()
        threadHandler1.post {
            binding.btnStart.text = "50%"
        }
    }

    private fun update1d() {
        val threadHandler2 = Handler(Looper.getMainLooper())
        threadHandler2.post {
            binding.btnStart.text = "50%"
        }
    }

    private fun update1e() {
        binding.btnStart.post {
            binding.btnStart.text = "50%"
        }
    }

    private fun update1f() {
        runOnUiThread {
            binding.btnStart.text = "50%"
        }
    }

    private fun post1a() {
        looperThread.handler.post {
            Util.run(TAG)
        }
    }

    private fun post1b() {
        // this starts an anonymous inner class which maintains a reference to the outer class
        // (MainActivity), so if MainActivity is destroyed, as when the screen is rotated, it's
        // not garbage collected, resulting in a memory leak.
        // a fix would be to create a Runnable class as a static inner class but you won't be
        // able to access the outer class (MainActivity)'s variables.
        // if you still need to access outer class' variables, then use a WeakReference
        val threadHandler = Handler(looperThread.looper)
        threadHandler.post {
            Util.run(TAG)
        }
    }

    private fun post1c() {
        // here a Message contains raw data (like the what field), which the Runnable doesn't
        // know how to handle
        val message = Message.obtain()
        message.what = TASK_A
        looperThread.handler.sendMessage(message)
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
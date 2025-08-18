package com.nesp.spiderx.runtime.utils

import java.util.LinkedList

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
class Looper {
    private val tasks = LinkedList<Runnable>()

    @Volatile
    private var isRunning = false

    fun loop() {
        isRunning = true
        while (isRunning) {
            synchronized(tasks) {
                while (tasks.isNotEmpty()) {
                    tasks.removeFirst().run()
                }
            }
        }
    }

    fun finish() {
        isRunning = false
        println("Looper finish")
    }

    fun post(task: Runnable) {
        synchronized(tasks) {
            tasks.addLast(task)
        }
    }

    companion object {

        private const val TAG = "Looper"

        val main: Looper by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Looper() }
    }
}
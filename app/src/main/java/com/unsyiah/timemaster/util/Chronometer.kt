package com.unsyiah.timemaster.util

import android.os.Handler
import android.os.Looper

class Chronometer {

    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var mRunning: Boolean = false
    private var mOnChronometerTickListener: () -> Unit = {}
    private val mDefaultDelay = 1000L
    private var mDelay = mDefaultDelay
    private var mTickFrequency = mDefaultDelay

    fun setOnChronometerTickListener(function: () -> Unit) {
        mOnChronometerTickListener = function
    }

    fun start(customDelay: Long = mDefaultDelay, customTickFrequency: Long = mDefaultDelay) {
        mRunning = true
        mDelay = customDelay
        mTickFrequency = customTickFrequency
        mHandler.postDelayed(mTickRunnable, mDelay)
    }

    fun stop() {
        mRunning = false
        mHandler.removeCallbacks(mTickRunnable)
    }

    private val mTickRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mRunning) {
                mOnChronometerTickListener()
                mHandler.postDelayed(this, mTickFrequency)
            }
        }
    }
}
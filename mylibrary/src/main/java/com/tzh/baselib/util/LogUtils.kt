package com.tzh.baselib.util

import android.util.Log

object LogUtils {
    private const val TAG = "LogUtils"

    @JvmStatic
    fun e(tag : String?,text : String){
        Log.e(tag.toDefault(TAG),text)
    }

    @JvmStatic
    fun d(tag : String?,text : String){
        Log.d(tag.toDefault(TAG),text)
    }

    @JvmStatic
    fun i(tag : String?,text : String){
        Log.i(tag.toDefault(TAG),text)
    }

    @JvmStatic
    fun w(tag : String?,text : String){
        Log.w(tag.toDefault(TAG),text)
    }

    @JvmStatic
    fun v(tag : String?,text : String){
        Log.v(tag.toDefault(TAG),text)
    }

    fun e(t: Throwable?) {
        Log.e(TAG, t.toString())
    }
}
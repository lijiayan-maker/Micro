package com.mycro.micro.utils

import android.util.Log

/**
 * Author: canyan.zhang
 * Email:canyan.zhang@xjmz.com
 * Date: 2025/4/25 21:51
 * Description: 日志管理类
 */

object LogUtils {

    private const val TAG = "ui_editor"
    private const val PRINT = true

    @JvmStatic
    fun d(tag: String, msg: String) {
        d(tag, msg, true)
    }

    @JvmStatic
    fun d(tag: String, msg: String, print: Boolean) {
        if (print) {
            Log.d("${TAG}_$tag", msg)
        }
    }

    @JvmStatic
    fun i(tag: String, msg: String) {
        if (PRINT) {
            Log.i("${TAG}_$tag", msg)
        }
    }

    @JvmStatic
    fun w(tag: String, msg: String) {
        if (PRINT) {
            Log.w("${TAG}_$tag", msg)
        }
    }

    @JvmStatic
    fun e(tag: String, msg: String) {
        if (PRINT) {
            Log.e("${TAG}_$tag", msg)
        }
    }

    @JvmStatic
    fun e(tag: String, msg: String, e: Exception) {
        if (PRINT) {
            Log.e("${TAG}_$tag", msg, e)
        }
    }
}
package com.mycro.micro.utils

import android.content.Context
import android.util.TypedValue

/**
 * Author: canyan.zhang
 * Date: 2025/4/21 02:10
 * Description: 工具类
 */

class Utils {
    companion object {
        /**
         * dp 转 px
         */
        @JvmStatic
        fun dp2px( context: Context, dp: Int): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
        }
    }


}
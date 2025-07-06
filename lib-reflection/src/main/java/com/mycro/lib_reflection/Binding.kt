package com.mycro.lib_reflection

import android.app.Activity
import android.view.View

/**
 * Author: canyan.zhang
 * Email:canyan.zhang@xjmz.com
 * Date: 2025/5/24 16:33
 * Description: YEAR!!
 */

class Binding {
    companion object {
        fun binding(activity: Activity) {
            val clazz = activity.javaClass
            val fields = clazz.declaredFields
            for (field in fields) {
                if (field.isAnnotationPresent(BindView::class.java)) {
                    val bindView = field.getAnnotation(BindView::class.java)
                    val viewId = bindView.value
                    try {
                        // 使用 Activity 的 findViewById 方法
                        val view = activity.findViewById<View>(viewId)
                        field.isAccessible = true
                        field.set(activity, view)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }
}
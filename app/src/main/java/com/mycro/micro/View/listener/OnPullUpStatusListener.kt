package com.mycro.micro.View.listener

/**
 * Author: canyan.zhang
 * Email:canyan.zhang@xjmz.com
 * Date: 2025/4/25 21:56
 * Description: 拖拽布局状态监听器
 */
interface OnPullUpStatusListener {
    /**
     * 拖拽结束
     * @param isOpen 是否是展开状态
     */
    fun onDragFinished(isOpen: Boolean)

    /**
     * 正在拖拽
     */
    fun onDragging()
}
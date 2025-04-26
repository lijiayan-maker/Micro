package com.mycro.micro.View.helper

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.mycro.micro.View.listener.GlobalOverScrollListener
import kotlin.math.abs

class SpringAnimationHelper(
    private val context: Context,
    private val targetView: View,
    private val overScrollListeners: MutableList<GlobalOverScrollListener>? = null,
    private val scrollOrientation: Int = SCROLL_VERTICALLY
) {
    companion object {
        const val SCROLL_VERTICALLY = 1
        const val SCROLL_HORIZONTALLY = 2
    }

    private val springAnim: SpringAnimation =
        if (scrollOrientation == SCROLL_VERTICALLY) SpringAnimation(targetView, SpringAnimation.TRANSLATION_Y)
        else SpringAnimation(targetView, SpringAnimation.TRANSLATION_X)

    private val springForce = SpringForce().apply {
        finalPosition = 0f
        stiffness = SpringForce.STIFFNESS_MEDIUM
        dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
    }

    private var lastTouchPos = 0f
    private var isDragging = false
    private var overScrollEnabled = true
    private var velocity = 0f
    private var overScrollEnable = true
    private var topEnable = true
    private var bottomEnable = true

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    init {
        springAnim.spring = springForce
    }

    fun setTopOverScrollEnable(enable: Boolean) {
        topEnable = enable
    }

    fun isTopOverScrollEnable(): Boolean = topEnable

    fun setBottomOverScrollEnable(enable: Boolean) {
        bottomEnable = enable
    }

    fun isBottomOverScrollEnable(): Boolean = bottomEnable

    fun onTouchEvent(event: MotionEvent): Boolean {
        if (!overScrollEnabled) return false

        val pos = if (scrollOrientation == SCROLL_VERTICALLY) event.y else event.x

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchPos = pos
                isDragging = false
                springAnim.cancel()
            }

            MotionEvent.ACTION_MOVE -> {
                val delta = pos - lastTouchPos
                if (!isDragging && abs(delta) > touchSlop) {
                    isDragging = true
                }

                if (isDragging) {
                    val currentTranslation = if (scrollOrientation == SCROLL_VERTICALLY)
                        targetView.translationY else targetView.translationX

                    val newTranslation = currentTranslation + delta / 2f
                    if (scrollOrientation == SCROLL_VERTICALLY)
                        targetView.translationY = newTranslation
                    else
                        targetView.translationX = newTranslation

                    overScrollListeners?.forEach {
                        it.onOverScroll(newTranslation)
                    }

                    lastTouchPos = pos
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                springAnim.setStartVelocity(velocity).start()
            }
        }

        return isDragging
    }

    fun fling(velocityX: Int, velocityY: Int) {
        if (!overScrollEnabled) return

        val v = if (scrollOrientation == SCROLL_VERTICALLY) velocityY.toFloat() else velocityX.toFloat()
        velocity = v.coerceIn(-8000f, 8000f) // 安全值
    }

    fun setOverScrollEnable(enable: Boolean) {
        overScrollEnabled = enable
    }

    fun isOverScrollEnable(): Boolean = overScrollEnabled

    fun isOverScrollDynamic(): Boolean {
        return (if (scrollOrientation == SCROLL_VERTICALLY)
            targetView.translationY
        else
            targetView.translationX) != 0f
    }

    fun addOnOverScrollListener(listener: GlobalOverScrollListener) {
        overScrollListeners?.add(listener)
    }

    fun removeOverScrollListener(listener: GlobalOverScrollListener) {
        overScrollListeners?.remove(listener)
    }

    fun clearOnOverScrollListener() {
        overScrollListeners?.clear()
    }
}

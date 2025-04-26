package com.mycro.micro.View.helper

import android.view.MotionEvent
import android.view.VelocityTracker
import com.mycro.micro.View.PullUpDrawerLayout

/**
 * Author: canyan.zhang
 * Email:canyan.zhang@xjmz.com
 * Date: 2025/4/25 21:27
 * Description: 配合 PullUpDrawerLayout 实现上拉抽屉交互效果，支持手势滑动、速度计算、抽屉拖动和动画控制。
 */

class PullUpDrawerHelper(private val mPullUpDrawerLayout: PullUpDrawerLayout) {

    private val mVelocityTracker: VelocityTracker = VelocityTracker.obtain()
    private var mIsClickView: Boolean = false
    private var mYVelocity: Int = 0

    fun onTouch(event: MotionEvent): Boolean {
        val action = event.action
        val rawY = event.rawY

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                if (mPullUpDrawerLayout.hasRunAnim()) return true
                mPullUpDrawerLayout.beginTranY(rawY)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (mPullUpDrawerLayout.isTouchBottom(rawY)) return true
                mPullUpDrawerLayout.setTranY(rawY)
                return true
            }

            else -> {
                if (mPullUpDrawerLayout.isTouchBottom(rawY)) {
                    onTouchUp(event)
                    return true
                }
                mPullUpDrawerLayout.endTranY(event, mYVelocity)
                return true
            }
        }
    }

    fun onTouchUp(event: MotionEvent) {
        mPullUpDrawerLayout.setTouching(false)
    }

    fun getTranslationY(): Float {
        return mPullUpDrawerLayout.translationY
    }

    private fun isTouchPointInView(x: Int, y: Int): Boolean {
        val location = IntArray(2)
        mPullUpDrawerLayout.getLocationOnScreen(location)
        val left = location[0]
        val top = location[1]
        val right = left + mPullUpDrawerLayout.measuredWidth
        val bottom = top + mPullUpDrawerLayout.measuredHeight
        return y in top..bottom && x in left..right
    }

    fun dispatchTouchEvent(ev: MotionEvent) {
        val action = ev.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                if (isTouchPointInView(ev.x.toInt(), ev.y.toInt())) {
                    mVelocityTracker.clear()
                    mVelocityTracker.addMovement(ev)
                    mIsClickView = true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (mIsClickView) {
                    mVelocityTracker.addMovement(ev)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (mIsClickView) {
                    mVelocityTracker.computeCurrentVelocity(300)
                    mYVelocity = mVelocityTracker.yVelocity.toInt()
                    mVelocityTracker.clear()
                }
            }
        }
    }
}
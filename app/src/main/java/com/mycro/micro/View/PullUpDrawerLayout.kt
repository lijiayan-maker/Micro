package com.mycro.micro.View

/**
 * Author: canyan.zhang
 * Email:canyan.zhang@xjmz.com
 * Date: 2025/4/25 21:32
 * Description: YEAR!!
 */

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.animation.PathInterpolatorCompat
import com.mycro.micro.View.listener.OnPullUpStatusListener
import com.mycro.micro.utils.LogUtils
import com.mycro.micro.R

class PullUpDrawerLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    companion object {
        private const val TAG = "PullUpDrawerLayout"
        private const val AMIM_DURATION = 300L
        private const val BOUNCE_ANIMATION_TENSION = 1.0f
    }

    private var mOriginalTranY = 0f
    private var mOriginalTouchY = 0f
    private val mTouchSlop: Int
    private var mHeight: Int
    private var mSecondHeight: Int = 0
    private var mBottomShowHeight = 0
    private var mTranYAnimator: ObjectAnimator? = null
    private var mThisDragTranYAnimator: ObjectAnimator? = null
    private var mIsOpen = false
    // 使用 PathInterpolatorCompat 自动适配不同 Android 版本
    private val mPathInterpolator = PathInterpolatorCompat.create(0.11f, 0.9f, 0.2f, 1f)
    private val mHideInterpolator = PathInterpolatorCompat.create(0.23f, 0.03f, 0.63f, 0.93f)
    private val mTitleLayoutRect = Rect()
    private var mOnPullUpStatusListener: OnPullUpStatusListener? = null
    private var mTouching = false
    private var mBackgroundAlphaAnimator: ValueAnimator? = null
    private var mHasAnim = true
    private var mHasMove = false
    private var mHandle: View? = null
    var mInterceptDoingScale = false

    init {
        mHeight = context.resources.getDimensionPixelSize(R.dimen.pull_up_drawerlayout_height)
        mTouchSlop = context.resources.getDimensionPixelSize(R.dimen.pull_up_drawerlayout_move_judgment)

        val upCorner = resources.getDimension(R.dimen.half_popup_radius)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mHandle = findViewById(R.id.handle)
        setWidgets()
    }

    private fun setWidgets() {
        translationY = mHeight.toFloat()
    }

    fun setLayoutHeight(height: Int) {
        mHeight = height
    }

    fun show(isAnim: Boolean) {
        if (mBottomShowHeight == 0) {
            mBottomShowHeight = resources.getDimensionPixelSize(R.dimen.pull_up_drawer_height)
        }
        mIsOpen = true
        mHasAnim = isAnim
        if (isAnim) {
            startAnim(true)
        } else {
            translationY = 0f
            onDragFinished(true)
        }
    }

    fun hide(isAnim: Boolean) {
        mHasAnim = isAnim
        if (isAnim) {
            startAnim(false)
        } else {
            translationY = (mHeight - mBottomShowHeight).toFloat()
            onDragFinished(false)
        }
        mIsOpen = false
    }

    fun packUp() {
        mThisDragTranYAnimator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, (mHeight - mBottomShowHeight).toFloat())
        mThisDragTranYAnimator?.apply {
            duration = 250L
            interpolator = mPathInterpolator
            start()
        }
    }

    private fun startAnim(isShow: Boolean) {
        mTranYAnimator?.removeAllListeners()
        mTranYAnimator?.cancel()

        mTranYAnimator = ObjectAnimator.ofFloat(
            this,
            View.TRANSLATION_Y,
            if (isShow) 0f else (mHeight - mBottomShowHeight).toFloat()
        ).apply {
            interpolator = if (isShow) mPathInterpolator else mHideInterpolator
            duration = AMIM_DURATION
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    onDragging()
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!isShow) mIsOpen = false
                    onDragFinished(mIsOpen)
                }

                override fun onAnimationCancel(animation: Animator) {
                    if (!isShow) mIsOpen = false
                    onDragFinished(mIsOpen)
                }

                override fun onAnimationRepeat(animation: Animator) {}
            })
            start()
        }
    }

    fun setOnPullUpStatusChanged(listener: OnPullUpStatusListener) {
        mOnPullUpStatusListener = listener
    }

    private fun onDragging() {
        resetSlideBackground()
        mOnPullUpStatusListener?.onDragging()
    }

    private fun onDragFinished(isOpen: Boolean) {
        mOnPullUpStatusListener?.onDragFinished(isOpen)
    }

    fun beginTranY(originalTouchY: Float) {
        mHasMove = false
        mOriginalTouchY = originalTouchY
        mThisDragTranYAnimator?.cancel()
        mThisDragTranYAnimator = null
        mOriginalTranY = translationY
    }

    fun setTranY(nowTouchY: Float) {
        if (mInterceptDoingScale || kotlin.math.abs(nowTouchY - mOriginalTouchY) < 50) return

        if (mOriginalTranY >= mHeight + 1) return

        val tranY = mOriginalTranY + nowTouchY - mOriginalTouchY
        mHasMove = true
        mTouching = true

        when {
            tranY <= 0 -> {
                translationY = 0f
                onDragFinished(true)
            }
            tranY >= (mHeight - mBottomShowHeight) -> {
                translationY = (mHeight - mBottomShowHeight).toFloat()
                onDragFinished(false)
            }
            else -> {
                translationY = tranY
                onDragging()
            }
        }
    }

    fun isTouchBottom(nowTouchY: Float): Boolean {
        val tranY = mOriginalTranY + nowTouchY - mOriginalTouchY
        val bottomY = mHeight - mBottomShowHeight
        val isTouchBottom = mOriginalTranY == bottomY.toFloat() && tranY >= bottomY && !mHasMove
        LogUtils.d(TAG, "isTouchBottom:$isTouchBottom mHasMove:$mHasMove")
        return isTouchBottom
    }

    fun endTranY(event: MotionEvent, velocity: Int) {
        if (mInterceptDoingScale) return

        mTouching = false
        val nowTranY = translationY
        val direction = if (nowTranY - mOriginalTranY > 0) 1 else -1
        val totalTranY = kotlin.math.abs(nowTranY - mOriginalTranY)
        val isFast = kotlin.math.abs(velocity) > 500

        mThisDragTranYAnimator = when {
            isFast -> ObjectAnimator.ofFloat(
                this,
                View.TRANSLATION_Y,
                if (velocity > 0) (mHeight - mBottomShowHeight).toFloat() else 0f
            ).apply {
                interpolator = if (velocity > 0) mHideInterpolator else mPathInterpolator
            }

            totalTranY in mTouchSlop.toFloat()..Float.MAX_VALUE -> ObjectAnimator.ofFloat(
                this,
                View.TRANSLATION_Y,
                if (direction == 1) (mHeight - mBottomShowHeight).toFloat() else 0f
            ).apply {
                interpolator = if (direction == 1) mHideInterpolator else mPathInterpolator
            }

            totalTranY in 1f..<mTouchSlop.toFloat() -> ObjectAnimator.ofFloat(
                this,
                View.TRANSLATION_Y,
                mOriginalTranY
            ).apply {
                interpolator = if (direction == 1) mPathInterpolator else mHideInterpolator
            }

            else -> {
                getGlobalVisibleRect(mTitleLayoutRect)
                if (mTitleLayoutRect.contains(event.rawX.toInt(), event.rawY.toInt()) && kotlin.math.abs(velocity) < 30) {
                    if (nowTranY != 0f) {
                        mThisDragTranYAnimator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, 0f).apply {
                            interpolator = mPathInterpolator
                        }
                    }
                }
                null
            }
        }

        mThisDragTranYAnimator?.apply {
            duration = AMIM_DURATION
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) = onDragging()
                override fun onAnimationEnd(animation: Animator) {
                    mIsOpen = translationY <= mTouchSlop
                    onDragFinished(mIsOpen)
                }

                override fun onAnimationCancel(animation: Animator) = onDragFinished(mIsOpen)
                override fun onAnimationRepeat(animation: Animator) {}
            })
            start()
        } ?: onDragFinished(mIsOpen)
    }

    fun setBottomShowHeight(bottomShowHeight: Int) {
        mBottomShowHeight = bottomShowHeight
    }

    fun isOpen(): Boolean = mIsOpen

    private fun resetSlideBackground() {
        mBackgroundAlphaAnimator?.let {
            if (it.isStarted) {
                it.removeAllListeners()
                it.cancel()
            }
        }
        background?.alpha = 255
    }

    fun startBackgroundAnimation(
        updateListener: ValueAnimator.AnimatorUpdateListener,
        updateWindowStatusListener: UpdateWindowStatusListener?,
        listener: Animator.AnimatorListener
    ) {
        if (mTouching) return

        if (mHasAnim) {
            mBackgroundAlphaAnimator?.removeAllListeners()
            if (mBackgroundAlphaAnimator == null) {
                mBackgroundAlphaAnimator = ValueAnimator.ofInt(255, 0).apply {
                    startDelay = 200
                    duration = 330
                }
            }
            mBackgroundAlphaAnimator?.apply {
                addUpdateListener(updateListener)
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        listener.onAnimationEnd(animation)
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        listener.onAnimationEnd(animation)
                    }
                })
                start()
            }
        } else {
            updateWindowStatusListener?.onUpdateWindowStatus()
        }
    }

    fun forceUpdateAnimation() {
        mHasAnim = true
    }

    fun setTouching(touching: Boolean) {
        mTouching = touching
    }

    fun hasRunAnim(): Boolean =
        translationY > 0 && (mHeight - mBottomShowHeight) > translationY

    fun setHandleVisibility(visibility: Int) {
        mHandle?.visibility = visibility
    }

    fun closeView() {
        mInterceptDoingScale = true
        ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, (mHeight - mBottomShowHeight).toFloat()).apply {
            interpolator = mHideInterpolator
            duration = AMIM_DURATION
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) = onDragging()
                override fun onAnimationEnd(animation: Animator) {
                    mIsOpen = translationY <= mTouchSlop
                    onDragFinished(mIsOpen)
                    mInterceptDoingScale = false
                    visibility = View.GONE
                }

                override fun onAnimationCancel(animation: Animator) {
                    onDragFinished(mIsOpen)
                    mInterceptDoingScale = false
                    visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animator) {}
            })
            start()
        }
    }

    fun moveViewByHand(distance: Int) {
        mInterceptDoingScale = true
        translationY = -distance.toFloat()
    }

    fun getAnimDuration(): Long = AMIM_DURATION

    interface UpdateWindowStatusListener {
        fun onUpdateWindowStatus()
    }
}

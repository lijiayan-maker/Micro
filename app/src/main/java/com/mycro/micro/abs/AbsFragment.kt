package com.mycro.micro.abs

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

/**
 * Author: canyan.zhang
 * Date: 2025/4/21 02:16
 * Description: Fragment切换过渡效果
 */

open class AbsFragment: Fragment() {
    private var mAlphaAnimation: ValueAnimator? = null
    private var mRootView: View? = null
    private var mIsOnViewCreated = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRootView = view
        mAlphaAnimation = ValueAnimator.ofFloat(0.00f, 0.33f, 0.67f, 1.00f).apply {
            duration = 500
        }
        mIsOnViewCreated = true
    }

    /**
     * fragment切换时需要执行过渡动画的view
     *
     * @return 需要执行过渡动画的视图元素
     */
    protected open fun getPageChangedAnimateView(): View? {
        return mRootView
    }

    protected open fun getPageAnimateView(): View? {
        return null
    }

    protected open fun alphaAnimationEnd() {}

    fun startEnterAnimation() {
        val animateView = getPageChangedAnimateView()
        val animation = mAlphaAnimation

        if (animateView != null && animation != null) {
            if (animation.isStarted) {
                animation.cancel()
            }

            animation.removeAllListeners()
            animation.removeAllUpdateListeners()

            animation.addUpdateListener { animator ->
                val value = animator.animatedValue as Float
                animateView.alpha = value

                if (value == 1f) {
                    alphaAnimationEnd()
                }
            }

            animation.start()
        }
    }

    fun isOnViewCreated(): Boolean {
        return mIsOnViewCreated
    }
}
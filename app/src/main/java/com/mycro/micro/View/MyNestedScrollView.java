package com.mycro.micro.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.mycro.micro.View.helper.PullUpDrawerHelper;
import com.mycro.micro.View.helper.SpringAnimationHelper;
import com.mycro.micro.View.listener.GlobalOverScrollListener;

/**
 * Author: canyan.zhang
 * Email:canyan.zhang@xjmz.com
 * Date: 2025/4/25 22:07
 * Description:
 */
public class MyNestedScrollView extends NestedScrollView {
    private final SpringAnimationHelper mSpringAnimationHelper;
    private boolean mShouldRequestDisallow = false;

    private PullUpDrawerHelper mPullUpDrawerHelper;
    private boolean mDownDrawer = false;

    private int mLastMotionY;

    public interface OverScrollListener extends GlobalOverScrollListener {
    }

    public MyNestedScrollView(@NonNull Context context) {
        this(context, null);
    }

    public MyNestedScrollView(@NonNull Context context,
                              @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs,
                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mSpringAnimationHelper = new SpringAnimationHelper(context, this, null,
                SpringAnimationHelper.SCROLL_VERTICALLY);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public void setOverScrollEnable(boolean enable) {
        mSpringAnimationHelper.setOverScrollEnable(enable);
    }

    public boolean isOverScrollEnable() {
        return mSpringAnimationHelper.isOverScrollEnable();
    }

    public void setTopOverScrollEnable(boolean enable) {
        mSpringAnimationHelper.setTopOverScrollEnable(enable);
    }

    public boolean isTopOverScrollEnable() {
        return mSpringAnimationHelper.isTopOverScrollEnable();
    }

    public void setBottomOverScrollEnable(boolean enable) {
        mSpringAnimationHelper.setBottomOverScrollEnable(enable);
    }

    public boolean isBottomOverScrollEnable() {
        return mSpringAnimationHelper.isBottomOverScrollEnable();
    }

    public void addOnOverScrollListener(@NonNull OverScrollListener overScrollListener) {
        mSpringAnimationHelper.addOnOverScrollListener(overScrollListener);
    }

    public void removeOverScrollListener(@NonNull OverScrollListener overScrollListener) {
        mSpringAnimationHelper.removeOverScrollListener(overScrollListener);
    }

    public void clearOnOverScrollListener() {
        mSpringAnimationHelper.clearOnOverScrollListener();
    }

    public SpringAnimationHelper getSpringAnimationHelper() {
        return mSpringAnimationHelper;
    }

    public void setShouldRequestDisallowInterceptTouchEventWhenOverScroll(boolean shouldRequestDisallow) {
        this.mShouldRequestDisallow = shouldRequestDisallow;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        if (!mDownDrawer) {
            if (mSpringAnimationHelper.onTouchEvent(event)) {
                ViewParent parent = getParent();
                if (parent != null && mShouldRequestDisallow) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
                return true;
            }
            try {
                return super.onTouchEvent(event);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mPullUpDrawerHelper != null && canMoveDrawer()) {
                    mDownDrawer = true;
                    mPullUpDrawerHelper.onTouch(ev);
                }
                mLastMotionY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mPullUpDrawerHelper != null && mDownDrawer) {
                    mPullUpDrawerHelper.onTouch(ev);
                }
                int y = (int) ev.getY();
                int deltaY = mLastMotionY - y;
                if (deltaY > 0 && mPullUpDrawerHelper.getTranslationY() == 0) {
                    mDownDrawer = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mPullUpDrawerHelper != null) {
                    if (mDownDrawer) {
                        mPullUpDrawerHelper.onTouch(ev);
                    }
                    mDownDrawer = false;
                    mPullUpDrawerHelper.onTouchUp(ev);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean canMoveDrawer() {
        return getScrollY() == 0 && !mSpringAnimationHelper.isOverScrollDynamic();
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY);
        mSpringAnimationHelper.fling(0, velocityY);
    }

    public void setPullUpDrawerHelper(PullUpDrawerHelper helper) {
        mPullUpDrawerHelper = helper;
    }
}

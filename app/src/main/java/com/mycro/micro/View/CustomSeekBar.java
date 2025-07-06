package com.mycro.micro.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import com.mycro.micro.R;
import com.mycro.micro.utils.LogUtils;

public class CustomSeekBar extends View {

    private static final String TAG = "CustomSeekBar";
    protected Paint mBackgroundPaint, mPaint, mCirclePaint, mClipPaint;
    protected int mSeekBarMinValue;
    protected int mSeekBarMaxValue;
    protected int mColor;
    protected int mBackgroundColor;
    protected int mCircleColor;
    protected int mRadius;
    protected int mWidth;
    protected int mHeight;
    protected int mScrollWidth;
    protected int mCurrentValue;
    protected int mValueRange;
    protected ValueListener mValueListener;
    protected PorterDuffXfermode mXfermode;
    protected RectF mTopRectF, mBottomRectF;
    protected TypedValue mTypedValue;
    protected float mThumbPosition; // 保存小圆球位置
    protected int mDefaultValue; // 默认节点
    protected int mCircleRadius; // 小圆球半径
    protected int mCircleY; // 小圆球y坐标
    protected int mClipCircleColor;

    private boolean mDisable = false;

    public CustomSeekBar(@NonNull Context context) {
        this(context, null);
    }

    public CustomSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void logMessage(String tag, String message) {
        LogUtils.d(tag, message);
    }

    protected void initCustomValues() {
        mDefaultValue = 0;
        mSeekBarMinValue = 0;
        mSeekBarMaxValue = 100;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        initCustomValues();

        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        mTopRectF = new RectF();
        mBottomRectF = new RectF();
        mTypedValue = new TypedValue();

        mBackgroundColor = getResources().getColor(R.color.seek_bar_background_color);
        getContext().getTheme().resolveAttribute(android.R.attr.colorPrimary, mTypedValue, true);
        mColor = mTypedValue.data;
        mCircleColor = 0x1F767680;
        mClipCircleColor = 0x33FFFFFF; // 20%透明度的白色
        mValueRange = mSeekBarMaxValue - mSeekBarMinValue;
        mRadius = getResources().getDimensionPixelSize(R.dimen.dp_25);
        mCurrentValue = (mSeekBarMaxValue + mSeekBarMinValue) / 2;

        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);

        mClipPaint = new Paint();
        mClipPaint.setColor(mClipCircleColor);
        mClipPaint.setStyle(Paint.Style.FILL);
        mClipPaint.setAntiAlias(true);

    }

    public void setCurrentValue(int value) {
        mCurrentValue = value;
        logMessage(TAG, "set mCurrentValue = " + mCurrentValue);
        getScrollWidth();
        // 设置完textTimeSize需要回调
        if (mValueListener != null) {
            mValueListener.onValueChanged(getCurrentValue());
        }
        invalidate();
    }

    protected int getCurrentValue() {
        // 浮点数转整型精度有缺失，加0.5f
        mCurrentValue = (int) (mSeekBarMinValue + ((float) mScrollWidth / mWidth) * mValueRange + 0.5f);
        logMessage(TAG, "get mCurrentValue= " + mCurrentValue);
        if (mCurrentValue < mSeekBarMinValue) return mSeekBarMinValue;
        if (mCurrentValue > mSeekBarMaxValue) return mSeekBarMaxValue;
        return mCurrentValue;
    }

    public int getScrollWidth() {
        mScrollWidth = (int) ((float) (mCurrentValue - mSeekBarMinValue) / (float) mValueRange * mWidth);
        logMessage(TAG, "mScrollWidth = " + mScrollWidth);
        return mScrollWidth;
    }

    public void setValueListener(ValueListener valueListener) {
        this.mValueListener = valueListener;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        mCircleY = mHeight / 2;
        mCircleRadius = mHeight / 10;
        getScrollWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int layerId = canvas.saveLayer(0, 0, mWidth, mHeight, null, Canvas.ALL_SAVE_FLAG);
        mBottomRectF.set(0, 0, mWidth, mHeight);
        canvas.drawRoundRect(mBottomRectF, mRadius, mRadius, mBackgroundPaint);
        mPaint.setXfermode(mXfermode);
        mTopRectF.set(0, 0, mScrollWidth, mHeight);
        canvas.drawRect(mTopRectF, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(layerId);

        mThumbPosition = (mDefaultValue - mSeekBarMinValue) / (float) mValueRange * mWidth;
        canvas.drawCircle(mThumbPosition, mCircleY, mCircleRadius, mCirclePaint);
        canvas.save();
        // 裁剪画布
        canvas.clipRect(mThumbPosition - mCircleRadius, mCircleY - mCircleRadius, mScrollWidth,mCircleY + mCircleRadius);
        // 绘制裁剪后的圆形（蓝色）
        canvas.drawCircle(mThumbPosition, mCircleY, mCircleRadius, mClipPaint);
        canvas.restore();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mDisable) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    setCurrentValue(event);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    setCurrentValue(event);
                    return true;
                case MotionEvent.ACTION_UP:
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private void setCurrentValue(MotionEvent event) {
        mScrollWidth = (int) event.getX();
        logMessage(TAG, "setScrollWidth = " + mScrollWidth);
        if (mValueListener != null) {
            mValueListener.onValueChanged(getCurrentValue());
        }
        invalidate();
    }

    public void setViewGray(boolean isGray) {
        if (isGray) {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            mPaint.setColorFilter(filter);
        } else {
            mPaint.setColorFilter(null);
        }
        invalidate();
    }

    /**
     * 禁用，背景颜色修改为40%透明度
     * 滑动条不允许拖动
     * @param isDisable
     */
    public void setViewDisable(boolean isDisable) {
        this.mDisable = isDisable;
        if (isDisable) {
            int transparentColor = ColorUtils.setAlphaComponent(mColor, Math.round(255 * 0.4f));
            mPaint.setColor(transparentColor);
        } else {
            mPaint.setColor(mColor);
        }
        invalidate();
    }


    public interface ValueListener {
        void onValueChanged(int size);
    }

}

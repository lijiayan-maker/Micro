package com.mycro.micro.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.icu.text.UnicodeSetSpanner;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;

public class FocusSunView extends View {


    private float sunWidth = 3.0f; //空心圆
    private float frameWidth = 4.0f; //对焦框
    private float lineWidth = 5.0f; //线条

    //框架
    private int paintColor = Color.WHITE; //对焦框颜色
    private Paint framePaint; //对焦框画笔
    private float frameRadius;
    private RectF frameRectF;

    private float frameLength;//控制边框线条的长度
    //空心圆
    private float centerOfCircle; //空心圆的中心（x轴）的位置，一直不变的
    private float circleRadius;
    private Paint sunPaint;
    private Paint moonPaint;
    private float circleY = -1f;//circleY是记录小太阳y轴的位置
    private float lastCircleY = 0f;

    //直线
    private boolean showLine = false;
    private float progress = 0.5f;
    private float realProcess = 0.5f;
    private float posY = 0f;
    private float curPosY = 0f;

    private float dp10;
    private float dp8; //小太阳的高度
    private float dp6;
    private float dp5;
    private float dp3;
    private float dp2;

    private Xfermode porterDuffDstOut;

    private float center = 0.5f;

    private float angle = 360f;// 360度

    private float borderWidth = frameWidth;

    //上下曝光限制
    private float upperExposureLimit = 2f;
    private float lowerExposureLimit = -2f;
    private float oldExposure = 0f;


    //CountDownTimer 类概述：
    //定时执行在一段时间后停止的倒计时，在倒计时执行过程中会在固定间隔时间得到通知（译者：触发onTick方法）
    private CountDownTimer countdown = null;
    private ValueAnimator focusAnimator = null;

    //曝光监听回调
    private OnExposureChangeListener onExposureChangeListener;

    public FocusSunView(Context context) {
        this(context, null);
    }

    public FocusSunView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusSunView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        //1.分析需求
        //2.自定义属性 attrs.xml ---------无需自定义属性，具体宽高，粗细和颜色不提供外部xml修改
        //3.onMeasure
        //4.获取自定义属性
        //5.绘制
    }

    private void init(Context context) {
        frameRectF = new RectF(0f,0f,0f,0f);
        framePaint = new Paint();
        framePaint.setAntiAlias(true); //抗锯齿
        framePaint.setColor(paintColor);

        sunPaint = new Paint();
        sunPaint.setAntiAlias(true);
        sunPaint.setColor(paintColor);

        moonPaint = new Paint();
        moonPaint.setAntiAlias(true);
        moonPaint.setColor(paintColor);

        dp10 = dp2px(context, 10f);
        dp8 = dp2px(context, 8f);
        dp6 = dp2px(context, 6f);
        dp5 = dp2px(context, 5f);
        dp3 = dp2px(context, 3f);
        dp2 = dp2px(context, 2f);

        porterDuffDstOut = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float width = MeasureSpec.getSize(widthMeasureSpec);
        float height = MeasureSpec.getSize(heightMeasureSpec);
        centerOfCircle = (width / 10f) * 9f;
        circleRadius = width / 30f;
        frameRadius = width / 5f;
        //对焦框四个角
        frameRectF.left = (width / 2f) - frameRadius;
        frameRectF.right = (width / 2f) + frameRadius;
        frameRectF.top = (height / 2f) - frameRadius;
        frameRectF.bottom = (height / 2f) + frameRadius;
        frameLength = frameRectF.height() / 4f;
    }


    /**
     * 设置曝光上限和下限
     */
    public void setExposureLimit(Float upperExposureLimit, Float lowerExposureLimit) {
        if (upperExposureLimit != null && lowerExposureLimit != null) {
            this.upperExposureLimit = upperExposureLimit;
            this.lowerExposureLimit = lowerExposureLimit;
        };
    }


    @SuppressLint("NewApi")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画对焦框 start
        framePaint.setStrokeWidth(borderWidth);
        //对焦框显而易见，其实就是画了八条线，想要边框间距更近一些，可以修改frameLength
        float[] points = new float[]{
                frameRectF.left, frameRectF.top, frameRectF.left, frameRectF.top + frameLength,
                frameRectF.left, frameRectF.top, frameRectF.left + frameLength, frameRectF.top,
                frameRectF.left, frameRectF.bottom, frameRectF.left, frameRectF.bottom - frameLength,
                frameRectF.left, frameRectF.bottom, frameRectF.left + frameLength, frameRectF.bottom,
                frameRectF.right, frameRectF.top, frameRectF.right, frameRectF.top + frameLength,
                frameRectF.right, frameRectF.top, frameRectF.right - frameLength, frameRectF.top,
                frameRectF.right, frameRectF.bottom, frameRectF.right, frameRectF.bottom - frameLength,
                frameRectF.right, frameRectF.bottom, frameRectF.right - frameLength, frameRectF.bottom
        };
        canvas.drawLines(points, framePaint);
        // 画对焦框 end
        // 画小太阳SeekBar start
        borderWidth = frameWidth;
        sunPaint.setStrokeWidth(borderWidth);
        // 画直线
        if (showLine) {
            if (circleY != circleRadius + dp8) { //判断当前小太阳是否处于最上方
                //如果不是的话，就会绘制小太阳上方的直线
                canvas.drawLine(centerOfCircle, 0f, centerOfCircle, (getHeight() * progress) - (circleRadius) - dp10, sunPaint);
            }
            if (circleY != getHeight() - (circleRadius) - dp8) {//判断是否处于最下方
                canvas.drawLine(centerOfCircle, (getHeight() * progress) + (circleRadius) + dp10, centerOfCircle, getHeight() * 1f, sunPaint);
            }
        }
        // 画空心圆
        borderWidth = sunWidth;
        sunPaint.setStrokeWidth(borderWidth);
        canvas.drawCircle(centerOfCircle, getHeight() * progress, circleRadius, sunPaint);
        // 画线条（太阳的散光）
        for (int i = 0; i < 8; i++){
            PointF startPointF = calculationPoint(angle - (i * 45f), circleRadius + dp3);
            PointF endPointF = calculationPoint(angle - (i * 45f), circleRadius + dp5);
            borderWidth = lineWidth;
            sunPaint.setStrokeWidth(borderWidth);
            canvas.drawLine(startPointF.x, startPointF.y, endPointF.x, endPointF.y, sunPaint);

        }

        // 画中间月亮效果
        borderWidth = sunWidth;
        moonPaint.setStrokeWidth(borderWidth);
        if (realProcess < center) {
            // 张弦月
            float left = centerOfCircle - ((circleRadius - dp2) * 2f) * Math.abs(realProcess - 0.5f);
            float top = (getHeight() * progress) - (circleRadius - dp2);
            float right = centerOfCircle + (((circleRadius - dp2) * 2f) * Math.abs(realProcess - 0.5f));
            float bottom = (getHeight() * progress) + (circleRadius - dp2);
            canvas.drawOval(left, top, right, bottom, moonPaint);
            canvas.drawArc(centerOfCircle - (circleRadius - dp2), (getHeight() * progress) - (circleRadius - dp2),
                    centerOfCircle + (circleRadius - dp2), (getHeight() * progress) + (circleRadius - dp2),
                    90f, 180f, false, moonPaint);
        } else if (realProcess == center) {
            // 下弦月
            canvas.drawArc(centerOfCircle - (circleRadius - dp2), (getHeight() * progress) - (circleRadius - dp2),
                    centerOfCircle + (circleRadius - dp2), (getHeight() * progress) + (circleRadius - dp2),
                    90f, 180f, false, moonPaint);
        } else {
            // 残月
            //save(): 用来保存Canvas的状态,save()方法之后的代码，可以调用Canvas的平移、放缩、旋转、裁剪等操作！
            int save = canvas.saveLayer(null, null);
            float left = centerOfCircle - (((circleRadius - dp2) * 2f) * Math.abs(realProcess - 0.5f));
            float top = (getHeight() * progress) - (circleRadius - dp2);
            float right = centerOfCircle + (((circleRadius - dp2) * 2f) * Math.abs(realProcess - 0.5f));
            float bottom = (getHeight() * progress) + (circleRadius - dp2);
            canvas.drawArc(centerOfCircle - (circleRadius - dp2 - 1), (getHeight() * progress) - (circleRadius - dp2 - 1),
                    centerOfCircle + (circleRadius - dp2 - 1), (getHeight() * progress) + (circleRadius - dp2 - 1),
                    90f, 180f, false, moonPaint);
            moonPaint.setXfermode(porterDuffDstOut);
            canvas.drawOval(left, top, right, bottom, moonPaint);
            moonPaint.setXfermode(null);
            //restore()：用来恢复Canvas之前保存的状态(可以想成是保存坐标轴的状态),防止save()方法代码之后对Canvas执行的操作，继续对后续的绘制会产生影响，通过该方法可以避免连带的影响
            canvas.restoreToCount(save);
        }

        // 画小太阳SeekBar end

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (circleY < 0f) {
                    circleY = getHeight() * progress;
                    lastCircleY = circleY;
                }
                posY = event.getY();
                paintColor = Color.WHITE;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                curPosY = event.getY();
                paintColor = Color.WHITE;
                if ((curPosY - posY > 0) || (curPosY - posY < 0)) {
                    showLine = true;
                    //重点计算 start
                    circleY = (curPosY - posY) + lastCircleY;
                    if (circleY >= getHeight() - circleRadius - dp8) {
                        circleY = getHeight() - circleRadius - dp8;
                    }
                    if (circleY < circleRadius + dp8) {
                        circleY = circleRadius + dp8;
                    }
                    realProcess = Math.round(((circleY - (circleRadius + dp8)) / ((getHeight() - circleRadius - dp8) - (circleRadius + dp8))) * 100f) / 100.0f;
                    progress = circleY / getHeight();
                    angle = 360f * realProcess;
                    float absolutelyProcess = Math.round(((((getHeight() - circleRadius - dp8) - (circleRadius + dp8)) - (circleY - (circleRadius + dp8))) / ((getHeight() - circleRadius - dp8) - (circleRadius + dp8))) * 100f) / 100.0f;
                    float step = upperExposureLimit - lowerExposureLimit;
                    float exposure = Math.round(((step * absolutelyProcess) + lowerExposureLimit) * 100f) / 100.0f;
                    if (onExposureChangeListener != null && oldExposure != exposure) {
                        oldExposure = exposure;
                        onExposureChangeListener.onExposureChangeListener(exposure);
                    }
                    RecycleCountDown();
                    //重点计算 end
                    invalidate();
                }

                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                showLine = false;
                invalidate();
                startCountdown(false);
                break;
            }
        }
        return true;
    }


    /**
     * 曝光回调
     */
    public interface OnExposureChangeListener {
        /**
         * 曝光回调
         * @param exposure 曝光
         */
        void onExposureChangeListener(float exposure);
    }

    /**
     * 设置曝光回调监听
     */
    public void setOnExposureChangeListener(OnExposureChangeListener onExposureChangeListener){
        this.onExposureChangeListener = onExposureChangeListener;
    }

    /**
     * 计算圆上任意点的坐标
     * @param angle 角度
     * @param radius 半径
     * @return 点坐标
     */
    public PointF calculationPoint(Float angle, Float radius) {
        float x = (float) ((centerOfCircle) + (radius) * Math.cos(angle * Math.PI / 180f));
        float y = (float) ((getHeight() * progress) + (radius) * Math.sin(angle * Math.PI / 180f));
        return new PointF(x, y);
    }


    /**
     * dp 转 px
     */
    private int dp2px(Context context, Float dpValue) {
        return (int) (dpValue * context.getResources().getDisplayMetrics().density + .5f);
    }

    /**
     * 点击动画/重置
     * 判断是否需要重置，刷新动画，回收
     */
    public void startCountdown(Boolean reset){
        if (reset) {
            progress = 0.5f;
            realProcess = 0.5f;
            circleY = getHeight() * progress;
            lastCircleY = circleY;
        }
        postInvalidate();
        RecycleCountDown();
        RecycleFocusAnimator();

        if (countdown == null) {
            if (!reset) {
                //5s倒计时
                CountDownMethod();
            } else {
                //将一个值从0平滑过渡到1或 从0过渡到5，再过渡到3，再过渡到10
                focusAnimator = ValueAnimator.ofFloat(0f, 1.3f, 1f).setDuration(500);
                if (focusAnimator != null) {
                    focusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        //定义动画更新时的具体操作
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float value = (float) animation.getAnimatedValue();
                            float left = (getWidth() / 2f) - frameRadius;
                            float right = (getWidth() / 2f) + frameRadius;
                            float top = (getHeight() / 2f) - frameRadius;
                            float bottom = (getHeight() / 2f) + frameRadius;
                            frameRectF.left = left - (((right - left) / 5f) - (((right - left) / 5f) * value));
                            frameRectF.right = right + ((right - left) / 5f - (((right - left) / 5f) * value));
                            frameRectF.top = top - ((bottom - top) / 5f - (((bottom - top) / 5f) * value));
                            frameRectF.bottom = bottom + ((bottom - top) / 5f - (((bottom - top) / 5f) * value));
                            postInvalidate();
                        }
                    });



                    //Animator类当中提供了一个addListener()方法，这个方法接收一个AnimatorListener，
                    // 我们只需要去实现这个AnimatorListener就可以监听动画的各种事件了。
                    focusAnimator.addListener(new AnimatorListenerAdapter() {
                        // onAnimationEnd()方法会在动画结束的时候调用
                        public void onAnimationEnd(Animator animation) {
                            focusAnimator = null;
                            CountDownMethod();
                        }
                    });

                    focusAnimator.start();
                }
            }
        }
    }

    private void CountDownMethod() {
        countdown = new  CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished >= 1000 && millisUntilFinished <= 2500) {
                    paintColor = Color.parseColor("#FFAAAAAA");
                    postInvalidate();
                }
            }

            @Override
            public void onFinish() {
                countdown = null;
                setVisibility(GONE);
            }
        }.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        focusAnimator.cancel();
        focusAnimator = null;
        countdown.cancel();
        countdown = null;
        super.onDetachedFromWindow();
    }

    private void RecycleCountDown() {
        if (countdown != null) {
            countdown.cancel();
            countdown = null;
        }
    }

    private void RecycleFocusAnimator() {
        if (focusAnimator != null) {
            focusAnimator.cancel();
            focusAnimator = null;
        }
    }
}

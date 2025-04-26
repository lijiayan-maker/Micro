package com.mycro.micro.View;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import com.mycro.micro.utils.Utils;
import com.mycro.micro.R;

/**
 * 封装一个圆形图片的ImageView
 */
//自定义View的步骤
//1.分析需求

@SuppressLint("AppCompatCustomView")
public class CircleImageView extends ImageView {

    private Paint mPaint;
    private int type;
    private int mBorderRadio;
    private int circleWidth;
    private int mRadius;


    private int DEFAULT_ROUND_SIZE = 10;
    private int Circle = 0 ;
    private int Round = 1 ;

    private Matrix matrix;
    private RectF rectf;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs){
        mPaint = new Paint();
        mPaint.setAntiAlias(true); //先开抗锯齿

        matrix = new Matrix();
        //2.自定义属性 attrs.xml
        //4.获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);
        mBorderRadio = typedArray.getDimensionPixelSize(R.styleable.CircleImageView_borderRadio, Utils.dp2px(context, DEFAULT_ROUND_SIZE));
        type = typedArray.getInt(R.styleable.CircleImageView_type, Round); //默认画圆角
        typedArray.recycle();
    }


    //3.onMeasure
    //方形图片就用系统的测量模式，圆形的需要自己重新实现测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(type == Circle){
            circleWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mRadius = circleWidth / 2;
            setMeasuredDimension(circleWidth, circleWidth);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if(type == Round){
            rectf = new RectF(0, 0 , getWidth(), getHeight());
        }
    }

    //5.onDraw绘制
    @Override
    protected void onDraw(Canvas canvas) {
        if(getDrawable() == null){
            return;
        }
        setBitmapShader();
        if(type == Circle){
            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        }else {
            canvas.drawRoundRect(rectf, mBorderRadio, mBorderRadio, mPaint);
        }
    }

    /**
     * 设置BitmapShader，渲染图像，使用图像为绘制图形着色
     */
    private void setBitmapShader() {

        double scale = 1;
        float dx = 0, dy = 0;

        //CLAMP是拉伸的效果
        Bitmap bitmap = ((BitmapDrawable)getDrawable()).getBitmap();
        BitmapShader bitmapShader = new BitmapShader(bitmap,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        //图片宽高
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        //视图宽高
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        if(type == Circle){
            int bSize = Math.min(bitmapWidth, bitmapHeight);
            scale = circleWidth * 1.0 / bSize;
        }else {
            scale = Math.max(viewHeight * 1.0f / bitmapHeight, viewWidth * 1.0f / bitmapWidth);
        }

        if(bitmapWidth * viewHeight > bitmapHeight * viewWidth){
            dx = (float) ((viewWidth - bitmapWidth * scale) * 0.5f);
        }else {
            dy = (float) ((viewHeight - bitmapHeight * scale) * 0.5f);
        }

        matrix.setScale((float) scale, (float) scale);
        matrix.postTranslate(dx, dy);

        mPaint.setShader(bitmapShader);
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable)
    {
        if (drawable instanceof BitmapDrawable)
        {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return bitmapDrawable.getBitmap();
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }


}

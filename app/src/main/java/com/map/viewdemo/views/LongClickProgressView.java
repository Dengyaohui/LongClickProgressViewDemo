package com.map.viewdemo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.map.viewdemo.R;


public class LongClickProgressView extends View {

    
    private Context mContext;//上下文
    private float mViewWidth;//当前View的宽度
    private float mViewHeight;//当前View的高度
    private float mCenterCircleWidth;//中间圆圈的宽(直径)
    private float mCenterCircleHeight;//中间圆圈的高(直径，等于宽)
    private Paint mProgressRingPaint; // 专门用于外沿圆环的画笔
    private Paint mBtnPaint; // 专门用于圆型按钮的画笔
    private float mRingWidth = 0;//圆环宽度
    private float mProgress = 0;//进度
    private int mTargetProgress = 100;  //最大进度
    private float mStartAngle = 180;//圆环进度开始角度(3点方向开始，往顺时针加)
    private int mRingColor;//圆环颜色
    private int mCenterColor;//中间圆型按钮颜色
    private int mCenterImage;//圆环中间图片
    private RectF mRectangleRectF;//View的显示范围矩形
    private RectF mBitmapRectF;//中心图片缩放后的显示范围矩形
    private RectF mProgressRectF;//进度圆环显示范围矩形
    private Bitmap mDrawBitmap;//用于画出中心图片的bitmap对象

    private Handler mHandler;  //计时用的handler
    private Runnable mRunnable;  //长按动作的执行
    private Runnable mCancelRunnable;  //取消动作的执行
    private static final int FINISH_TIME = 1000;

    
    
    private OnLongClickStateListener mOnLongClickStateListener;

    //设置回调
    public void setOnLongClickStateListener(OnLongClickStateListener onLongClickStateListener) {
        this.mOnLongClickStateListener = onLongClickStateListener;
    }

    public LongClickProgressView(Context context) {
        this(context,null);
    }

    public LongClickProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LongClickProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        //初始化自定义属性
        initAttrs(context, attrs);
        //初始化
        init();
    }

    /**
     * 设置中心图片
     * @param centerImageId 图片id（R.mipmap.xxxx or R.drawable.xxxx）
     */
    public void setCenterImage(int centerImageId) {
        this.mCenterImage = centerImageId;
        mDrawBitmap = BitmapFactory.decodeResource(mContext.getResources(),mCenterImage);
        invalidate();
    }

    /**
     * 设置中心颜色
     * @param
     */
    public void setCenterColor(int centerColorId) {
        this.mCenterColor = centerColorId;
        invalidate();
    }

    /**
     * 控制圆环进度
     * @param progress 进度（0-100f）
     */
    public void setProgress(float progress) {
        this.mProgress = progress;
        invalidate();
    }

    /**
     * 设置圆环开始角度
     * @param startAngle 开始角度
     */
    public void setStartAngle(float startAngle) {
        this.mStartAngle = startAngle;
        invalidate();
    }

    /**
     * 设置圆环颜色
     * @param ringColor 圆环颜色
     */
    public void setRingColor(int ringColor) {
        this.mRingColor = ringColor;
        mProgressRingPaint.setColor(mRingColor);  //设置画笔颜色
        invalidate();
    }

    /**
     * 设置圆环宽度
     * @param ringWidth 圆环宽度（单位：dp)
     */
    public void setRingWidth(float ringWidth) {
        this.mRingWidth = dp2px(mContext, ringWidth);
        invalidate();
    }

    /**
     * 初始化自定义属性
     * @param context
     * @param attrs
     */
    private void initAttrs(Context context, @Nullable AttributeSet attrs){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LongClickProgressView);
        BitmapDrawable mCenterImageDrawable = (BitmapDrawable) array.getDrawable(R.styleable.LongClickProgressView_centerDrawable);//自定义属性，可在XML内设置drawable中心图片
        if (mCenterImageDrawable != null){
            //XML布局设置的图片资源作为中心图片
            mDrawBitmap = mCenterImageDrawable.getBitmap();
        }
    }

    /**
     * 初始化
     */
    private void init() {
        //默认颜色
        mRingColor = Color.parseColor("#FF3C4A");
        mCenterColor = Color.parseColor("#FF3C4A");

        //设置中间圆圈大小默认为60dp*60dp
//        mCenterCircleWidth = dp2px(mContext, 60);
//        mCenterCircleHeight = dp2px(mContext, 60);

        //初始化圆环画笔
        mProgressRingPaint = new Paint();
        mProgressRingPaint.setAntiAlias(true); //启用抗锯齿
        mProgressRingPaint.setColor(mRingColor);
        mProgressRingPaint.setStyle(Paint.Style.STROKE);//圆弧
        mProgressRingPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressRingPaint.setFilterBitmap(true); //对位图进行滤波处理

        //初始化按钮画笔
        mBtnPaint = new Paint();
        mBtnPaint.setAntiAlias(true); //启用抗锯齿
        mBtnPaint.setFilterBitmap(true);  //对位图进行滤波处理

        mRectangleRectF = new RectF();//View的范围矩形
        mBitmapRectF = new RectF();//中心图片缩放后的范围矩形
        mProgressRectF = new RectF();//进度条显示范围矩形
//        mDrawBitmap = BitmapFactory.decodeResource(mContext.getResources(),mCenterImage);//中心图片，如果以图片为中心按钮，则设置对象，否则以画笔画圆

        mHandler = new Handler();
        //环形进度条自动增加逻辑
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mProgress += 1;
                setProgress(mProgress);
                //更新进度的接口回调
                if (mOnLongClickStateListener != null){
                    mOnLongClickStateListener.onProgress(mProgress);
                }
                if (mProgress < mTargetProgress){
                    mHandler.postDelayed(this, 1);
                }else {
                    //当环形进度条达到100，取消循环，进度置零，调用接口的完成回调
                    mProgress = 0;
                    if (mOnLongClickStateListener != null){
                        mOnLongClickStateListener.onFinish();
                    }
                }
            }
        };

        //取消动作的逻辑
        mCancelRunnable = new Runnable() {
            @Override
            public void run() {
                setProgress(mProgress);
                //当进度为0时，取消循环
                if (mProgress <= 0){
                    return;
                }else if (mProgress < 10){
                    //当进度降低到较低状态时，减缓降低的速度，每次减2
                    mProgress -= 2;
                }else {
                    //进度较高时，进度条减少的速度加快，每次减7
                    mProgress -= 7;
                }

                if (mProgress > 0){
                    mHandler.postDelayed(this, FINISH_TIME / 100);
                }else {
                    //当环形进度条达到0，再次手动置零，调用接口的取消回调，并返回进度回调参数
                    mProgress = 0;
                    if (mOnLongClickStateListener != null){
                        mOnLongClickStateListener.onCancel();
                    }
                }
                //更新进度的接口回调
                if (mOnLongClickStateListener != null){
                    mOnLongClickStateListener.onProgress(mProgress);
                }
            }
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
//        Log.e("TAG", "onTouchEvent: " + mProgress );
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.post(mRunnable);
                mHandler.removeCallbacks(mCancelRunnable);
//                Log.e("TAG", "onTouchEvent: ACTION_DOWN");
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(mRunnable);
                mHandler.post(mCancelRunnable);
//                Log.e("TAG", "onTouchEvent: ACTION_UP");
                break;
            case MotionEvent.ACTION_MOVE:
//                float x = motionEvent.getX();
//                float y = motionEvent.getY();
//                if (this.getLeft() > x  || this.getTop() > y || this.getRight() < x || this.getBottom() < y){
//                    mHandler.removeCallbacks(mRunnable);
//                    setProgress(0);
//                }
//                Log.e("TAG", "onTouchEvent: ACTION_MOVE");
                break;
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //算出矩形顶点坐标
        mRectangleRectF.set(0, 0, mViewWidth, mViewHeight);

        //中心原点固定为60dp*60dp，size为中心图片与View边距的距离
        float side = (mViewWidth - mCenterCircleWidth) / 2;

        mProgressRingPaint.setStrokeWidth(mRingWidth);//圆环宽度

        if (mProgress == 0){
            mBitmapRectF.set(side, side, mViewWidth - side, mViewHeight - side);
        }else {
            //增加mViewWidth / 20作为缩放
            float scaleSize = (float) ((mViewWidth + mViewHeight) / 2 * 0.05);
            mBitmapRectF.set(side - scaleSize,side - scaleSize,
                    mViewWidth - side + scaleSize, mViewWidth - side + scaleSize);

            //圆环进度条的顶点坐标,范围比圆圈超出：圆圈坐标 + mViewWidth/40
            mProgressRectF.set(side - scaleSize - mRingWidth - mViewWidth/40,side - scaleSize - mRingWidth - mViewHeight/40,
                    mViewWidth - (side - scaleSize) + mRingWidth + mViewWidth/40, mViewHeight - (side - scaleSize) + mRingWidth + mViewHeight/40);
        }

        //画中心图片
        if (mDrawBitmap != null){
            //设置了中心图片，画出bitmap
            canvas.drawBitmap(mDrawBitmap, null, mBitmapRectF, mBtnPaint);
        }else {
            //没有设置中心图片，手动画圆
            mBtnPaint.setStyle(Paint.Style.FILL);//实心圆型
            mBtnPaint.setStrokeCap(Paint.Cap.ROUND);
            mBtnPaint.setColor(mCenterColor);  //设置颜色
            canvas.drawArc(mBitmapRectF, 0, 360, true, mBtnPaint);  //画出360度实心圆
        }

        //计算进度所占圆环百分比(0-1.0f)
        float progressPercent = mProgress / 100;
        //计算进度所占旋转角度
        float roateAngle = progressPercent * 360;
        //计算圆环直径
        float ringDiameter = mViewWidth - mRingWidth * 2;
        //计算圆环圆心x
        float x0 = mViewWidth / 2.0f;
        //计算圆环圆心y
        float y0 = mViewHeight / 2.0f;
        //计算圆环半径
        float r = ringDiameter / 2;

        if (mProgress > 0 && mProgress <= 1) {
            //当进度大于0并且小于等于1时

            //计算开始角度startAngle在圆环上的点
            PointF pointF1 = calculatePointOfTheCircle(x0, y0, r, mStartAngle);
            //画圆点
            canvas.drawCircle(pointF1.x, pointF1.y, mRingWidth / 4, mProgressRingPaint);
        } else {
            //当进度大于1时

            //画圆环
            canvas.drawArc(mProgressRectF, mStartAngle, roateAngle, false, mProgressRingPaint);
        }
    }


    /**
     * 根据圆心、半径、角度计算在圆上的点
     * @param x0 圆心x
     * @param y0 圆心y
     * @param r 半径
     * @param angle 角度（弧度）
     * @return 圆上的点
     */
    private PointF calculatePointOfTheCircle(float x0, float y0, float r, float angle) {
        float radian = angle * 3.1415926f / 180;
        float x = (float) (x0 + Math.cos(radian) * r);
        float y = (float) (y0 + Math.sin(radian) * r);
        return new PointF(x, y);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mViewWidth = w;
        mViewHeight = h;
        //设置中间圆圈大小为View的6/8
        mCenterCircleWidth = mViewWidth * 6 / 8;
        mCenterCircleHeight = mViewHeight * 6 / 8;
        //根据宽高设置默认初始圆环宽度
        if(mRingWidth == 0){
            mRingWidth = (mViewWidth + mViewHeight) / 2 / 30;  //默认15分之一View的高
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * dp转px（保持精度）
     * @param context
     * @param dp
     * @return
     */
    public float dp2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    /**
     * sp转px （保持精度）
     * @param context
     * @param sp
     * @return
     */
    public float sp2px(Context context, float sp) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * fontScale + 0.5f;
    }

    /**
     * 长按完成和取消的接口
     */
    public interface OnLongClickStateListener {
        void onFinish();
        void onProgress(float progress);
        void onCancel();
    }
}

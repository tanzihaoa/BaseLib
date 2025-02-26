package com.tzh.baselib.view.function

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller
import com.tzh.baselib.util.DpToUtil
import com.tzh.baselib.util.MathUtil
import java.lang.Math.abs
import kotlin.math.roundToInt

class MyVerticalRulerView : View {
    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    //************ Paint *************
    private val mTextPaint: Paint   // 刻度数值

    private val mCenterTextPaint: Paint   // 刻度数值

    private val mScalePaint: Paint   // 刻度线 - 暗色
    private val mCenterScalePaint: Paint    // 刻度线 - 亮色


    //************** 刻度参数 **************
    private var mCurrentValue = 50.0f   // 刻度尺 当前值.
    private var mPerValue = 1f          // 刻度精度, 例如: 身高为1cm  体重为0.1Kg
    private var mMaxValue = 200f        // 刻度最大值上限
    private var mMinValue = 0f        // 刻度最小值
    private var mTotalScale = 0         // 共有多少条 刻度
    private var mOffset = 0f            // 刻度尺当前值 位于尺子总刻度的位置
    private var mMaxOffset = 0          // 所有刻度 共有多长

    private var mScaleSpace = 20f       // 刻度2条线之间的距离
    private var mScaleWidth = 4f        // 刻度线的宽度(粗细)
    private var mScaleHeight = 40f      // 刻度线的长度(基础长度)

    private var mCenterColor = Color.parseColor("#72b7f9")  // 亮色刻度 色值
    private var mTextColor = Color.parseColor("#333333")    // 文字, 普通刻度 的颜色

    var mVlaueListener: ((Float) -> Unit)? = null    // 滑动后数值回调

    // 刻度值 文字参数
    private var mTextSize = 16f         // 尺子刻度下方数字 textsize

    private val mTextWidth: Float      // 刻度数值文字 的高度
    private var mTextLoc = 0f           // 文字基线的位置;


    //************** 手势滑动相关 **************
    /**
     * Scroller是一个专门用于处理滚动效果的工具类   用mScroller记录/计算View滚动的位置，
     * 再重写View的computeScroll()，完成实际的滚动
     */
    private val mScroller: Scroller = Scroller(context)

    /**
     * 启动 fling 的滑动最小速率;
     */
    private val mMinVelocity: Int = ViewConfiguration.get(context).scaledMinimumFlingVelocity

    /**
     * 惯性滑动速度追踪类
     */
    private var mVelocityTracker: VelocityTracker? = null

    private var mLastY = 0      // 滑动初始 按下坐标值
    private var mMove: Int = 0  // 滑动X轴 偏移量;

    //指针与刻度 之间的距离
    private val mTextDistance by lazy {
        DpToUtil.dip2px(context,4f)
    }

    companion object{
        const val TAG = "MyRulerView"
    }

    init {
        // 初始化 Paint
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.textSize = DpToUtil.dip2px(context,mTextSize).toFloat()
            it.color = mTextColor

            // 获取文字高度
            val value = mMaxValue.toString()
            val rect = Rect()
            it.getTextBounds(value,0,value.length,rect)
            mTextWidth = rect.width().toFloat()
        }

        mCenterTextPaint  = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.textSize = DpToUtil.dip2px(context,mTextSize).toFloat()
            it.color = mCenterColor
        }

        mScalePaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.strokeWidth = mScaleWidth
            it.color = mTextColor
        }

        mCenterScalePaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.color = mCenterColor
            it.strokeCap = Paint.Cap.ROUND
        }
    }

    /**
     * 设置标尺参数. 在未设置之前, View将会显示空白
     * @param currentValue  默认值
     */
    fun setValue(currentValue: Float) {
        mCurrentValue = currentValue

        calculation()
    }

    /**
     * 设置标尺参数. 在未设置之前, View将会显示空白
     * @param currentValue  默认值
     * @param minValue      最大数值 (标尺上限)
     * @param maxValue      最小的数值 (标尺下限)
     * @param per           标尺精度  如 1:表示 每2条刻度差为1; 0.1:表示 每2条刻度差为0.1
     */
    fun setValue(currentValue: Float, minValue: Float, maxValue: Float, per: Float) {
        mCurrentValue = currentValue
        mMaxValue = maxValue
        mMinValue = minValue
        mPerValue = per * 10.0f

        calculation()
    }

    /**
     * 计算数值
     */
    private fun calculation(){
        // 计算总刻度数. 两头都有线,所以+1
        mTotalScale = ((mMaxValue - mMinValue) * 10 / mPerValue + 1).toInt()

        // 刻度总长度, 负数
        mMaxOffset = (-(mTotalScale - 1) * mScaleSpace).toInt()

        // 算当前位置. 也是负数
//        mOffset = (mMinValue - mCurrentValue) / mPerValue * mScaleSpace * 10

        mOffset = (-(mMaxValue - mCurrentValue)) * 10.0f / mPerValue * mScaleSpace
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heithtSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            // 这里 mScaleHeight * 2 是因为分为长线和短线. 亮色线最长为 2倍 基础长度;
            val width = paddingStart + paddingEnd + mTextWidth * 1.2 + mTextDistance + mScaleHeight * 2
            setMeasuredDimension(width.toInt(),heithtSpecSize)
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            val width = paddingStart + paddingEnd + mTextWidth * 1.2 + mTextDistance + mScaleHeight * 2
            setMeasuredDimension(width.toInt(), heithtSpecSize)
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, heithtSpecSize)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            mTextLoc = (h  - paddingBottom).toFloat()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var xInView: Float          // 刻度 在View中的理论位置; (如果超出屏幕, 则不需要去绘制)
        var realScaleHeight: Float   // 真实的刻度长度; 中间亮色部分 长度越大;
        var value: String           // 刻度值 的数值文字
        val halfWidth = height / 4   // 一半 View 的宽度. 也就是: 当前选中刻度的位置

        for (i in mTotalScale downTo  0) {
            xInView = halfWidth + mOffset + i * mScaleSpace
            if (xInView < paddingTop || xInView > (height - paddingBottom)) {
                // 超出 View 外的刻度线. 就不画了
                continue
            }
            val dis = abs(xInView - halfWidth)
            if (dis < mScaleSpace * 0.5) {
                // 当刻度距离中间较近时, 绘制量色刻度线. 计算刻度的长度 及 刻度的粗细;
                val rate = 1 - dis / (mScaleSpace * 3)
                realScaleHeight = (1.1f + rate * 0.5f) * mScaleHeight
                mCenterScalePaint.strokeWidth = mScaleWidth * (1.5f * rate + 1.5f)

                // 绘制刻度线
                canvas.drawLine(width - paddingBottom - mTextWidth * 1.2f - mTextDistance - realScaleHeight,xInView,
                    width - mTextWidth * 1.2f - mTextDistance - paddingBottom,xInView,
                    mCenterScalePaint
                )

                // 整数时 绘制 刻度值
                val mun = (mMaxValue - i * mPerValue / 10)
                if(mPerValue  % 10 == 0f){
                    value = mun.toInt().toString()
                }else{
                    value = com.tzh.baselib.util.MathUtil.roundedNumber(mun,1).toString()
                }

                val rect = Rect()
                mCenterTextPaint.getTextBounds(value,0,value.length,rect)
                canvas.drawText(
                    value,
                    (width - mTextWidth * 1.2f),xInView + rect.height() / 2, mCenterTextPaint
                )
            }else{
                // 当刻度超出中间值过多时, 绘制暗色刻度线
                realScaleHeight = mScaleHeight
                if(i % 10 == 0){
                    realScaleHeight +=  mScaleHeight / 2
                }
                canvas.drawLine(width - mTextWidth * 1.2f - mTextDistance - paddingBottom- realScaleHeight,xInView,
                    width - paddingBottom - mTextWidth * 1.2f - mTextDistance , xInView,
                    mScalePaint
                )

                if (i % 10 == 0 && dis > mScaleSpace * 5) {
                    // 整数时 绘制 刻度值
                    value = (mMaxValue - i * mPerValue / 10).toInt().toString()
                    val rect = Rect()
                    mCenterTextPaint.getTextBounds(value,0,value.length,rect)
                    canvas.drawText(
                        value,
                        (width - mTextWidth * 1.2f),xInView + rect.height() / 2, mTextPaint
                    )
                }
            }

        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val xPosition = event.y.toInt()
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mScroller.forceFinished(true)
                mLastY = xPosition
                mMove = 0
            }
            MotionEvent.ACTION_MOVE -> {
                // 计算移动值, 让标尺跟随移动
                mMove = mLastY - xPosition
                changeMoveAndValue()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 根据滑动速率, 判断 启动 Scroller 的惯性滑动;
                countVelocityTracker()
                return false
            }
        }
        mLastY = xPosition
        return true
    }

    /**
     * 根据滑动速率, 启动惯性滑动
     */
    private fun countVelocityTracker() {
        mVelocityTracker!!.computeCurrentVelocity(1000) //初始化速率的单位
        val xVelocity = mVelocityTracker!!.xVelocity //当前的速度
        if (abs(xVelocity) > mMinVelocity) {
            mScroller.fling(0, 0,
                xVelocity.toInt(), 0, Int.MIN_VALUE, Int.MAX_VALUE, 0, 0
            )
        } else {
            countMoveEnd()
        }
    }

    /**
     * 滑动结束后，若是指针在2条刻度之间时，改变mOffset 让指针正好在刻度上。
     */
    private fun countMoveEnd() {
        mOffset -= mMove.toFloat()
        if (mOffset <= mMaxOffset) {
            mOffset = mMaxOffset.toFloat()
        } else if (mOffset >= 0) {
            mOffset = 0f
        }
        mLastY = 0
        mMove = 0
        mCurrentValue =
            mMaxValue - (abs(mOffset) / mScaleSpace).roundToInt() * mPerValue / 10.0f
        mOffset = (-(mMaxValue - mCurrentValue)) * 10.0f / mPerValue * mScaleSpace
        notifyValueChange()
        postInvalidate()
    }

    /**
     * 滑动后的操作
     */
    private fun changeMoveAndValue() {
        mOffset -= mMove.toFloat()
        if (mOffset <= mMaxOffset) {
            mOffset = mMaxOffset.toFloat()
            mMove = 0
            mScroller.forceFinished(true)
        } else if (mOffset >= 0) {
            mOffset = 0f
            mMove = 0
            mScroller.forceFinished(true)
        }
        mCurrentValue =
            mMaxValue - (abs(mOffset) / mScaleSpace).roundToInt() * mPerValue / 10.0f
        notifyValueChange()
        postInvalidate()
    }

    private fun notifyValueChange() {
        mCurrentValue = com.tzh.baselib.util.MathUtil.roundedNumber(mCurrentValue,1)
        mVlaueListener?.invoke(mCurrentValue)
    }

    override fun computeScroll() {
        //mScroller.computeScrollOffset()返回 true表示滑动还没有结束
        if (mScroller.computeScrollOffset()) {
            if (mScroller.currY == mScroller.finalY) {
                countMoveEnd()
            } else {
                val xPosition = mScroller.currY
                mMove = mLastY - xPosition
                changeMoveAndValue()
                mLastY = xPosition
            }
        }
    }
}
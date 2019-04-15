package com.ting.canvasstudy

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class SplashView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    //旋转圆的画笔
    lateinit var mPaint: Paint
    //扩散圆的画笔
    lateinit var mHolePaint: Paint
    //属性动画
    lateinit var mValueAnimator: ValueAnimator
    //背景色
    private val mBackgroundColor: Int = Color.WHITE
    private lateinit var mCircleColors: IntArray

    //旋转圆的中心坐标
    private var mCenterX = 0f
    private var mCenterY = 0f

    //斜对角线长度的一半，扩散圆最大半径
    private var mDistance = 0f

    //6个小球的半径
    private val mCircleRadius = 18f
    //旋转大圆的半径
    private val mRotateRadius = 90f

    //当前大圆的旋转角度
    private var mCurrentRotateAngle = 0f
    //当前大圆的半径
    private val mCurrentRotateRadius = mRotateRadius
    //扩散圆的半径
    private val mCurrentHoleRadius = 0f
    //表示旋转动画的时长
    private val mRotateDuration = 1200L

    private var mState: SplashState? = null

    init {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mHolePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mHolePaint.style = Paint.Style.STROKE
        mHolePaint.color = mBackgroundColor

        mCircleColors = context.resources.getIntArray(R.array.splash_circle_colors)
    }

    /**
     * 获取圆心坐标
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = w * 1f / 2
        mCenterY = h * 1f / 2
        mDistance = (Math.hypot(w.toDouble(), h.toDouble()) / 2).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mState==null){
            mState = RotateState()
        }
        mState?.drawState(canvas)
    }

    abstract inner class SplashState {
        abstract fun drawState(canvas: Canvas)
    }

    //1.旋转
    inner class RotateState : SplashState() {
        init {
            //旋转一周
            mValueAnimator = ValueAnimator.ofFloat(0f,(Math.PI*2).toFloat())
            //播放两遍
            mValueAnimator.repeatCount = 2
            //播放时长
            mValueAnimator.duration = mRotateDuration
            //线性插值器
            mValueAnimator.interpolator = LinearInterpolator()
            //更新监听
            mValueAnimator.addUpdateListener {
                mCurrentRotateAngle = it.animatedValue as Float
                invalidate()
            }
            mValueAnimator.start()
            //运行
        }
        override fun drawState(canvas: Canvas) {
            //绘制背景
            drawBackground(canvas)
            //绘制6个小球
            drawCircles(canvas)
        }
    }

    private fun drawCircles(canvas: Canvas) {
        val rotateAngle = Math.PI * 2 / mCircleColors.size
        for (color in mCircleColors.withIndex()) {
            //x = centerX+cos(a)*r
            //y = centerY+cos(a)*r
            val angle = rotateAngle * color.index+mCurrentRotateAngle
            val cx = mCenterX + Math.cos(angle) * mRotateRadius
            val cy = mCenterY + Math.sin(angle) * mRotateRadius
            mPaint.color = color.value
            canvas.drawCircle(cx.toFloat(), cy.toFloat(), mCircleRadius, mPaint)
        }

    }

    fun drawBackground(canvas: Canvas) {
        canvas.drawColor(mBackgroundColor)
    }
    //2.扩散聚合
    //3.水波纹
}

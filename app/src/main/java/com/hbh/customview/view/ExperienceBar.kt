package com.hbh.customview.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator

class ExperienceBar
    @JvmOverloads
    constructor(context : Context,
                attrs : AttributeSet? = null,
                defStyleAttr : Int = 0,
                defStyleRes : Int = 0)
    : View(context, attrs, defStyleAttr, defStyleRes) {

    //整个View的宽度
    private var mViewWidth = 0F
    //整个View的高度
    private var mViewHeight = 0F
    //内部经验条的宽度
    private var mLineWidth = 0F
    //内部经验条的高度
    private var mLineHeight = 0F
    //内部经验条的左边距
    private var mLineLeft = 0F
    //内部经验条的上边距
    private var mLineTop = 0F
    //经验条的圆角
    private var mRadius = 0F
    //等级圆点的间隔
    private var mPointInterval = 0F
    //当前经验值
    private var mExperience = 0
    //每一等级占总长的百分比
    private var mLevelPercent = 1F
    //经验条百分比（相对于总进度）
    private var mExperiencePercent = 1F
    //当前等级
    private var mCurrentLevel = 0
    //升级所需要的经验列表
    private val mLevelList = mutableListOf<Int>()

    //各种颜色值
    private val mPointColor = Color.parseColor("#E1E1E1")
    private val mLineColor = Color.parseColor("#666666")
    private val mShaderStartColor = Color.parseColor("#18EFE2")
    private val mShaderEndColor = Color.parseColor("#0CF191")
    private val mStrokeColor = Color.parseColor("#323232")
    //各种颜色值

    //各种画笔
    private val mStrokePaint by lazy {
        Paint().apply {
            color = mStrokeColor
        }
    }
    private val mShaderPaint by lazy {
        Paint().apply {
            color = mShaderStartColor
        }
    }
    private val mLinePaint by lazy {
        Paint().apply {
            color = mLineColor
        }
    }
    private val mLevelAchievedPaint by lazy {
        Paint().apply {
            color = mShaderEndColor
        }
    }
    private val mLevelNotAchievedPaint by lazy {
        Paint().apply {
            color = mPointColor
        }
    }
    //各种画笔

    fun updateExperience(experience : Int) {
        if (mLevelList.isEmpty() || experience == mExperience) return
        mExperience = experience
        startAnimator(mExperiencePercent, computerLevelInfo())
    }

    /**
     * 外界设置等级信息
     */
    fun setLevelInfo(experience : Int, list : List<Int>) {
        mExperience = experience
        mLevelList.clear()
        mLevelList.addAll(list)
        computerPointInterval()
        startAnimator(0F, computerLevelInfo())
    }

    //动画相关
    private var mAnimator : ValueAnimator? = null
    //动画时长
    private val mAnimatorDuration = 500L
    //插值器
    private val mInterpolator by lazy { DecelerateInterpolator() }
    //动画值回调
    private val mAnimatorListener by lazy {
        ValueAnimator.AnimatorUpdateListener {
            mExperiencePercent = it.animatedValue as Float
            invalidate()
        }
    }

    /**
     * 开始经验条动画
     */
    private fun startAnimator(start : Float, end : Float) {
        mAnimator?.cancel()
        mAnimator = ValueAnimator.ofFloat(start, end).apply {
            duration = mAnimatorDuration
            interpolator = mInterpolator
            addUpdateListener(mAnimatorListener)
            start()
        }
    }
    //动画相关

    /**
     * 根据等级信息，计算出要绘制的经验条相关参数,并将最终的进度条比例返回
     */
    private fun computerLevelInfo() : Float {
        if (mLevelList.isNotEmpty()) {
            mCurrentLevel = 0
            for (value in mLevelList) {
                if (mExperience >= value) mCurrentLevel++
                else break
            }
            if (mCurrentLevel < mLevelList.size) {
                mLevelPercent = 1F / mLevelList.size
                val lastLevelExperience = if (mCurrentLevel > 0) mLevelList[mCurrentLevel - 1] else 0
                val currentLevelPercent = (mExperience - lastLevelExperience) /
                        (mLevelList[mCurrentLevel] - lastLevelExperience).toFloat()
                return (mCurrentLevel + currentLevelPercent) * mLevelPercent
            }
        }
        return 1F
    }

    /**
     * 计算各个等级点之间的间隔
     */
    private fun computerPointInterval() {
        if (mLineWidth > 0F || mLevelList.isNotEmpty()) {
            mPointInterval = mLineWidth / mLevelList.size
        }
    }

    /**
     * 测量各种尺寸
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        mViewWidth = (width - paddingStart - paddingEnd).toFloat()
        val height = mViewWidth / 20 + paddingTop + paddingBottom
        mViewHeight = mViewWidth / 20
        mRadius = mViewHeight

        mLineHeight = mViewHeight / 3
        mLineTop = (mViewHeight - mLineHeight) / 2

        mLineWidth = mViewWidth - mLineTop * 2
        mLineLeft = mLineTop
        setShaderColor()
        computerPointInterval()

        setMeasuredDimension(width, height.toInt())
    }

    /**
     * 设置经验条的渐变色
     */
    private fun setShaderColor() {
        mShaderPaint.shader = LinearGradient(0F, 0F, mLineWidth, 0F,
            mShaderStartColor, mShaderEndColor, Shader.TileMode.CLAMP)
    }

    /**
     * 绘制View
     */
    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(paddingStart.toFloat(), paddingTop.toFloat())
        drawBackground(canvas)
        drawExperienceBar(canvas)
        drawLevelPoint(canvas)
        canvas.restore()
    }

    /**
     * 绘制背景边框
     */
    private fun drawBackground(canvas: Canvas) {
        canvas.drawRoundRect(0F, 0F, mViewWidth, mViewHeight, mRadius, mRadius, mStrokePaint)
    }

    /**
     * 绘制经验条
     */
    private fun drawExperienceBar(canvas: Canvas) {
        val save = canvas.saveCount
        canvas.save()
        canvas.translate(mLineLeft, mLineTop)

        //绘制经验条底部背景
        canvas.drawRoundRect((mLineWidth * mExperiencePercent - mLineHeight).coerceAtLeast(0F),
            0F, mLineWidth, mLineHeight, mRadius, mRadius, mLinePaint)
        //绘制渐变的经验条
        canvas.drawRoundRect(0F, 0F, mLineWidth * mExperiencePercent, mLineHeight,
            mRadius, mRadius, mShaderPaint)

        canvas.restoreToCount(save)
    }

    /**
     * 绘制等级分割点
     */
    private fun drawLevelPoint(canvas: Canvas) {
        if (mLevelList.size > 1) {
            val save = canvas.saveCount
            canvas.save()
            canvas.translate(mLineLeft, 0F)

            //等级圆点的圆心Y轴坐标(由于经验条是水平的，所以所有Y轴坐标都一样)
            val cy = mViewHeight / 2
            //总共有n - 1个等级圆点，所以从1开始画，以达成的等级大圆点，未达成就是小圆点
            for (level in 1 until mLevelList.size) {
                val achieved = mExperiencePercent >= mLevelPercent * level
                canvas.drawCircle(mPointInterval * level, cy,
                    if (achieved) mLineHeight else mLineHeight / 2,
                    if (achieved) mLevelAchievedPaint else mLevelNotAchievedPaint)
            }

            canvas.restoreToCount(save)
        }
    }

}
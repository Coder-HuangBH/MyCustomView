package com.example.testtxlive.cloudMusic

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import androidx.annotation.IntDef
import com.example.testtxlive.cloudMusic.BorderLineInterval.Companion.COMPACT
import com.example.testtxlive.cloudMusic.BorderLineInterval.Companion.MODERATE
import com.example.testtxlive.cloudMusic.BorderLineInterval.Companion.SPARSE
import com.example.testtxlive.cloudMusic.BorderLineSheep.Companion.FAST
import com.example.testtxlive.cloudMusic.BorderLineSheep.Companion.NORMAL
import com.example.testtxlive.cloudMusic.BorderLineSheep.Companion.SLOW
import com.hbh.customview.R
import com.hbh.customview.util.UIUtil
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

class MusicAnimationView @JvmOverloads constructor(
    context : Context,
    attrs : AttributeSet? = null,
    defStyle : Int = 0
) : FrameLayout(context, attrs, defStyle){

    /** 中心图片占整个View的比例 */
    private val mImageSizeRatio = .5F
    /** 中心图片的实际尺寸 */
    private var mImageSize = 0
    /** 中心图片边框线占图片的比例 */
    private val mImageBorderWidthRatio = .03F
    /** 中心图片边框线颜色 */
    private val mImageBorderColor = Color.parseColor("#80FFFFFF")
    /** 中心图片旋转角度 */
    private var mImageRotation = 0F

    /**
     * 动画标志位（关键！！！）
     * 当它为true时，开始动画，中心图片旋转，外圈边框线旋转、扩大，且可添加；
     * 当它从true转false时，中心图片停止旋转，但是外圈的边框线不会停止，而是继续扩大至消失；
     * 当它为false时，不会再添加新的边框线，直到边框线全部消失后，停止动画
     */
    private var mAnimationFlag = true

    /** 边框线list */
    private val mBorderLines = mutableListOf<BorderLine>()
    /** 边框线粗细 */
    private var mBorderLineWidth = 2.0
    /** 边框线扩大速度 */
    @BorderLineSheep
    var mBorderLineGrowSheep = SLOW
    /** 边框线间隔 */
    @BorderLineInterval
    var mBorderLineIntervalDistance = COMPACT
    /** 边框线画笔 */
    private val mBorderLinePaint by lazy {
        Paint().apply {
            color = Color.parseColor("#80FF4777")
            strokeWidth = UIUtil.dip2px(context, mBorderLineWidth).toFloat()
        }
    }

    /** 中心圆形图片 唯一的子View */
    val mImage : CircleImageView by lazy {
        CircleImageView(context, attrs, defStyle).apply {
            layoutParams = LayoutParams(0, 0, Gravity.CENTER)
            setImageResource(R.drawable.ic_launcher_background)
            borderColor = mImageBorderColor
        }
    }

    init {
        addView(mImage)
    }

    /** 记录最后的动画值 */
    private var mLastAnimatorVal = 0F
    /** 中心圆形图片旋转一周的时间 */
    private var mAnimatorDuration = 20000L
    /** 动画 */
    private val mAnimator : ValueAnimator =
        ValueAnimator.ofFloat(360F).apply {
            duration = mAnimatorDuration
            interpolator = null
            repeatMode = ValueAnimator.RESTART
            repeatCount = -1
            addUpdateListener {
                val currentVal = it.animatedValue as Float
                //计算出需要旋转的度数
                val diffVal = if (mLastAnimatorVal <= currentVal) currentVal - mLastAnimatorVal
                              else currentVal + 360F - mLastAnimatorVal
                mLastAnimatorVal = currentVal

                processImageTransition(diffVal)
                processBorderLines(diffVal)

                invalidate()
            }
        }

    /**
     * 如果标志位为true，则旋转中心圆形图片
     */
    private fun processImageTransition(diffVal: Float) {
        if (mAnimationFlag) {
            mImageRotation = (mImageRotation + diffVal) % 360F
            mImage.rotation = mImageRotation
        }
    }

    /**
     * 处理边框线的变化
     */
    private fun processBorderLines(diffVal: Float) {
        if (mImageSize == 0) return
        //迭代器处理每条边框线  对边框线进行扩大、旋转  超出屏幕的移除
        val iterator = mBorderLines.iterator()
        while (iterator.hasNext()) {
            val line = iterator.next()
            line.radius += mBorderLineGrowSheep
            line.degree = (line.degree - diffVal) % 360
            //因为是逆时针旋转  可能为负角度  负角度情况加360变为正的
            if (line.degree < 0) line.degree += 360
            //超出屏幕的移除
            if (line.radius * 2 > width) iterator.remove()
        }
        //如果没有边框线
        if (mBorderLines.isEmpty()) {
            //标志位为true  直接添加一条边框线
            if (mAnimationFlag) {
                mBorderLines.add(BorderLine(mImageSize / 2F,
                    Random.nextInt(5, 15).toFloat(), Random.nextInt(360).toFloat()))
            } else {
                //真正停止动画
                mAnimator.cancel()
            }
        } else {
            //最后一条边框线与中心圆形图片的距离大于设定好的间隔，添加一条边框线
            if (mBorderLines.last().radius * 2 - mImageSize >= mBorderLineIntervalDistance) {
                if (mAnimationFlag) {
                    mBorderLines.add(BorderLine(mImageSize / 2F,
                        Random.nextInt(5, 15).toFloat(), Random.nextInt(360).toFloat()))
                }
            }
        }
    }

    /**
     * 测量View的尺寸，这里忽略了内外边距，这里应该用不上内外边距。
     * 用最简便的方式写的，如果对内外边距有需求，需要进行修改。
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val layoutWidth = MeasureSpec.getSize(widthMeasureSpec)
        val layoutHeight = MeasureSpec.getSize(heightMeasureSpec)
        val size = min(layoutWidth, layoutHeight)

        mImageSize = (size * mImageSizeRatio).toInt()
        mImage.measure(MeasureSpec.makeMeasureSpec(mImageSize, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(mImageSize, MeasureSpec.EXACTLY))
        mImage.borderWidth = (mImageSize * mImageBorderWidthRatio).toInt()

        setMeasuredDimension(size, size)
    }

    /**
     * 开始动画
     */
    fun startAnimation() {
        mAnimator.cancel()
        mAnimationFlag = true
        mLastAnimatorVal = 0F
        mAnimator.start()
    }

    /**
     * 停止动画(边框线不会立即停止)
     */
    fun stopAnimation() {
        mAnimationFlag = false
    }

    /**
     * 因为是继承自FrameLayout(ViewGroup)，所以在dispatchDraw这里绘制边框线。
     * 如果是在draw或者OnDraw里面绘制的话，需要设置setWillNotDraw(false)或者是设置一个背景，否则绘制会被直接跳过(绘制优化机制)
     */
    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?.run {
            val viewRadius = width / 2F
            for (line in mBorderLines) {
                mBorderLinePaint.alpha = ((viewRadius - line.radius) / viewRadius * 255).toInt()
                mBorderLinePaint.style = Paint.Style.STROKE
                drawCircle(viewRadius, viewRadius, line.radius, mBorderLinePaint)

                //point
                mBorderLinePaint.style = Paint.Style.FILL
                calculatePointOnCircle(viewRadius, viewRadius, line.radius, line.degree).run {
                    drawCircle(x.toFloat(), y.toFloat(), line.pointRadius, mBorderLinePaint)
                }
            }
        }
    }

    /**
     * 计算边框线上的点经过旋转后的具体坐标
     * 折腾一下午算不出来，这算法是AI写的，我也没看懂。。。
     */
    private fun calculatePointOnCircle(cx : Float, cy : Float, radius: Float, angleDegrees: Float): Point {
        val angleRadians = Math.toRadians(angleDegrees.toDouble())
        val x = cx + radius * cos(angleRadians)
        val y = cy + radius * sin(angleRadians)
        return Point(x.toInt(), y.toInt())
    }
}

data class BorderLine(var radius : Float, val pointRadius : Float, var degree : Float)

@Retention(AnnotationRetention.SOURCE)
@IntDef(COMPACT, MODERATE, SPARSE)
annotation class BorderLineInterval {
    companion object {
        const val COMPACT = 100
        const val MODERATE = 200
        const val SPARSE = 300
    }
}

@Retention(AnnotationRetention.SOURCE)
@IntDef(SLOW, NORMAL, FAST)
annotation class BorderLineSheep {
    companion object {
        const val SLOW = 1
        const val NORMAL = 2
        const val FAST = 3
    }
}
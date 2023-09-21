package com.hbh.customview.view.levelRecyclerView

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.hbh.customview.util.UIUtil
import kotlin.math.abs
import kotlin.math.max


class LevelRecyclerView @JvmOverloads constructor(
    context : Context,
    attrs : AttributeSet? = null,
    defStyleAttr : Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private val TAG = LevelRecyclerView::class.java.simpleName
    private val mSnapHelper = PagerSnapHelper()
    private val mLayoutManager = LinearLayoutManager(context, HORIZONTAL, false)
    //等级修改回调
    var levelListener : OnLevelChangeListener? = null
    //当前item索引
    private var lastPosition = 0

    init {
        mSnapHelper.attachToRecyclerView(this)
        layoutManager = mLayoutManager

        addItemDecoration(
            LevelDividerItemDecoration(
            UIUtil.dip2px(context, 16.0),
            UIUtil.dip2px(context, 4.0))
        )

        addOnScrollListener(object : OnScrollListener() {
            //系数最大值
            private val maxFactor = .45F

            /**
             * RecyclerView滚动
             */
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val first = mLayoutManager.findFirstVisibleItemPosition()
                val last = mLayoutManager.findLastVisibleItemPosition()
                val parentCenter = recyclerView.width / 2F
                for (i in first..last) {
                    setItemTransform(i, parentCenter)
                }
                changeSnapView()
            }

            /**
             * RecyclerView滚动状态改变
             */
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == SCROLL_STATE_IDLE) {
                    changeSnapView()
                }
            }

            /**
             * 对item进行各种变换
             * 目前是缩放与透明度变换
             */
            private fun setItemTransform(position : Int, parentCenter : Float) {
                mLayoutManager.findViewByPosition(position)?.run {
                    val factor = calculationViewFactor(left.toFloat(), width.toFloat(), parentCenter)
                    val scale = 1 + factor
                    scaleX = scale
                    scaleY = scale
                    alpha = 1 - maxFactor + factor
                }
            }

            /**
             * 计算当前item的缩放与透明度系数
             * item的中心离recyclerView的中心越远，系数越小（负相关）
             */
            private fun calculationViewFactor(left: Float, width : Float, parentCenter : Float) : Float {
                val viewCenter = left + width / 2
                val distance = abs(viewCenter - parentCenter) / width
                return max(0F, (1F - distance) * maxFactor)
            }

            /**
             * 修改当前居中的item，把当前等级回调给外界
             */
            private fun changeSnapView() {
                mSnapHelper.findSnapView(mLayoutManager)?.let {
                    mLayoutManager.getPosition(it).let { position ->
                        if (lastPosition != position) {
                            lastPosition = position
                            levelListener?.onLevelChange(position)
                        }
                    }
                }
            }
        })
    }

    /**
     * 方法的原实现其实就是LinearLayoutManager内部创建了一个LinearSmoothScroller去进行滚动，
     * 现在我们创建一个CenterSmoothScroller类去继承LinearSmoothScroller，
     * 重写它的calculateDtToFit方法，calculateDtToFit用于计算滚动距离，而calculateSpeedPerPixel计算滚动速度
     */
    override fun smoothScrollToPosition(position : Int) {
        if (position == lastPosition) return
        if (position < 0 || position >= (adapter?.itemCount ?: 0)) return

        mLayoutManager.startSmoothScroll(
            CenterSmoothScroller(context).apply {
                targetPosition = position
            }
        )
    }

    interface OnLevelChangeListener {
        fun onLevelChange(position : Int)
    }

    class CenterSmoothScroller(context: Context?) : LinearSmoothScroller(context) {

        override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int,
                                      boxEnd: Int, snapPreference: Int): Int {
            return boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)
        }

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
            return super.calculateSpeedPerPixel(displayMetrics) * 3F
        }
    }
}
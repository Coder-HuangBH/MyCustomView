package com.hbh.customview.view.levelRecyclerView

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

//创建一个LevelDividerItemDecoration类继承ItemDecoration
//构造参数需要传入分割线的水平长度与高度，分割线的颜色为可选参数
class LevelDividerItemDecoration @JvmOverloads constructor(
    private val itemDividerHorizontalMargin : Int,
    private val dividerHeight : Int,
    dividerColor : Int = Color.parseColor("#3A3A3C")
) : ItemDecoration() {

    //分割线Drawable
    private val mDivider = ColorDrawable(dividerColor)
    //分割线绘制区域
    private val mBounds = Rect()

    /**
     * 计算item的分割线需要的尺寸，就是一个偏移量，可简单看成外边距
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val parentWidth = parent.measuredWidth
        val itemWidth = view.layoutParams.width
        val lastPosition = parent.adapter?.itemCount?.minus(1) ?: 0
        //针对首尾两个item计算它们的左右边距，用parentWidth - itemWidth再除2，可以使item刚好到达RecyclerView的中间
        when (parent.getChildAdapterPosition(view)) {
            0 -> {
                outRect.set(((parentWidth - itemWidth) * 0.5).toInt(), 0, itemDividerHorizontalMargin, 0)
            }
            lastPosition -> {
                outRect.set(itemDividerHorizontalMargin, 0, ((parentWidth - itemWidth) * 0.5).toInt(), 0)
            }
            else -> outRect.set(itemDividerHorizontalMargin, 0, itemDividerHorizontalMargin, 0)
        }
    }

    /**
     * 绘制分割线
     */
    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)

        canvas.save()
        val top = (parent.height - dividerHeight) / 2
        val bottom = top + dividerHeight
        if (parent.clipToPadding) {
            canvas.clipRect(parent.paddingLeft, top, parent.width - parent.paddingRight, bottom)
        }

        //RecyclerView宽度
        val parentWidth = parent.measuredWidth
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val item = parent.getChildAt(i)
            //item宽度
            val itemWidth = item.measuredWidth
            //获取item的Rect，包含它的外边距(包含上面设置进去的偏移量)
            parent.layoutManager!!.getDecoratedBoundsWithMargins(item, mBounds)

            //左边分割线
            if (i == 0 && mBounds.width() > itemWidth + itemDividerHorizontalMargin * 2) {
                mDivider.setBounds(0, top, mBounds.right - itemWidth - itemDividerHorizontalMargin, bottom)
            } else {
                mDivider.setBounds(mBounds.left, top, mBounds.left + itemDividerHorizontalMargin, bottom)
            }
            mDivider.draw(canvas)

            //右边分割线
            if (i == childCount - 1 && mBounds.width() > itemWidth + itemDividerHorizontalMargin * 2) {
                mDivider.setBounds(mBounds.left + itemWidth + itemDividerHorizontalMargin, top, parentWidth, bottom)
            } else {
                mDivider.setBounds(mBounds.right - itemDividerHorizontalMargin, top, mBounds.right, bottom)
            }
            mDivider.draw(canvas)
        }
        canvas.restore()
    }
}
package com.hbh.customview.util

import android.content.Context

object UIUtil {

    fun dip2px(context: Context, dpValue: Double): Int {
        val density: Float = context.resources.displayMetrics.density
        return (dpValue * density + 0.5).toInt()
    }

    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }
}
package com.hbh.customview.view.cloudMusic

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.testtxlive.cloudMusic.BorderLineInterval.Companion.COMPACT
import com.example.testtxlive.cloudMusic.BorderLineInterval.Companion.MODERATE
import com.example.testtxlive.cloudMusic.BorderLineInterval.Companion.SPARSE
import com.example.testtxlive.cloudMusic.BorderLineSheep.Companion.FAST
import com.example.testtxlive.cloudMusic.BorderLineSheep.Companion.NORMAL
import com.example.testtxlive.cloudMusic.BorderLineSheep.Companion.SLOW
import com.example.testtxlive.cloudMusic.MusicAnimationView
import com.hbh.customview.R

class MusicActivity : AppCompatActivity() {

    private lateinit var music1 : MusicAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        music1 = findViewById<MusicAnimationView>(R.id.music1).apply {
            Glide.with(this@MusicActivity)
                .load("https://img.es123.com/uploadimg/image/20231027/20231027141045_71380.png")
//                .load("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fsafe-img.xhscdn.com%2Fbw1%2Ff47b4992-2be7-4cf3-88ed-d301c776cc7d%3FimageView2%2F2%2Fw%2F1080%2Fformat%2Fjpg&refer=http%3A%2F%2Fsafe-img.xhscdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1701785846&t=da73c7f4909236b3f57953140692d1e3")
                .into(mImage)
        }

        findViewById<View>(R.id.btn_start).apply {
            setOnClickListener {
                music1.startAnimation()
            }
        }

        findViewById<View>(R.id.btn_stop).apply {
            setOnClickListener {
                music1.stopAnimation()
            }
        }

        findViewById<View>(R.id.btn_sheep_slow).apply {
            setOnClickListener {
                music1.mBorderLineGrowSheep = SLOW
            }
        }

        findViewById<View>(R.id.btn_sheep_normal).apply {
            setOnClickListener {
                music1.mBorderLineGrowSheep = NORMAL
            }
        }

        findViewById<View>(R.id.btn_sheep_fast).apply {
            setOnClickListener {
                music1.mBorderLineGrowSheep = FAST
            }
        }

        findViewById<View>(R.id.btn_interval_compact).apply {
            setOnClickListener {
                music1.mBorderLineIntervalDistance = COMPACT
            }
        }

        findViewById<View>(R.id.btn_interval_moderate).apply {
            setOnClickListener {
                music1.mBorderLineIntervalDistance = MODERATE
            }
        }

        findViewById<View>(R.id.btn_interval_sparse).apply {
            setOnClickListener {
                music1.mBorderLineIntervalDistance = SPARSE
            }
        }
    }
}
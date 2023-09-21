package com.hbh.customview

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.hbh.customview.view.ExperienceBar
import com.hbh.customview.view.levelRecyclerView.LevelRecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initExperienceBar()

        initLevelRecyclerView()
    }

    private fun initExperienceBar() {
        val experience_bar = findViewById<ExperienceBar>(R.id.experience_bar)

        val a = 5
        val b = listOf(10,50,100,250,500,1000)
        val btn_test1 = findViewById<Button>(R.id.btn_test1).apply {
            setOnClickListener {
                experience_bar.setLevelInfo(a, b)
            }
        }
        val btn_test2 = findViewById<Button>(R.id.btn_test2).apply {
            var index = 0
            val c = listOf(888, 188)
            setOnClickListener {
                experience_bar.updateExperience(c[(index++) % c.size])
            }
        }
    }

    private fun initLevelRecyclerView() {
        val rv_level = findViewById<LevelRecyclerView>(R.id.rv_level)

        val list = mutableListOf<Int>()
        list.add(R.drawable.icon_vip_level_0)
        list.add(R.drawable.icon_vip_level_1)
        list.add(R.drawable.icon_vip_level_2)
        list.add(R.drawable.icon_vip_level_3)
        list.add(R.drawable.icon_vip_level_4)
        list.add(R.drawable.icon_vip_level_5)
        list.add(R.drawable.icon_vip_level_6)
        list.add(R.drawable.icon_vip_level_7)
        list.add(R.drawable.icon_vip_level_8)
        list.add(R.drawable.icon_vip_level_9)
        list.add(R.drawable.icon_vip_level_10)

        rv_level.adapter = object : BaseQuickAdapter<Int, QuickViewHolder>() {
            override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: Int?) {
                holder.getView<ImageView>(R.id.iv_image).run {
                    setImageResource(item!!)
                    setOnClickListener {
                        rv_level.smoothScrollToPosition(position)
                    }
                }
            }

            override fun onCreateViewHolder(context: Context, parent: ViewGroup,
                                            viewType: Int): QuickViewHolder =
                QuickViewHolder(R.layout.layout_item_level, parent)

        }.apply {
            submitList(list)
        }
    }
}
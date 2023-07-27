package com.hbh.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.hbh.customview.view.ExperienceBar
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}
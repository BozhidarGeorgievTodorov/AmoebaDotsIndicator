package com.bgt.amoebadotsindicator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bgt.amoebadotsindicator.AmoebaDotsIndicator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val indicator = findViewById<AmoebaDotsIndicator>(R.id.indicator)

        viewPager.adapter = DemoPagerAdapter(this)
        indicator.attachToViewPager(viewPager)
    }
}
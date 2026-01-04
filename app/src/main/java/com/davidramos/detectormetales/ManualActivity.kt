package com.davidramos.detectormetales

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.viewpager2.widget.ViewPager2
import com.davidramos.detectormetales.databinding.ActivityManualBinding

class ManualActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManualBinding
    private lateinit var adapter: ManualPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityManualBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupIndicators()
    }

    private fun setupViewPager() {
        adapter = ManualPagerAdapter(this)
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
            }
        })
    }

    private fun setupIndicators() {
        val dots = Array(adapter.itemCount) { i ->
            val dot = layoutInflater.inflate(R.layout.item_indicator_layout, binding.indicators, false)
            binding.indicators.addView(dot)
            dot
        }
        updateIndicators(0)
    }

    private fun updateIndicators(index: Int) {
        for (i in 0 until binding.indicators.childCount) {
            val dot = binding.indicators.getChildAt(i)
            dot.isSelected = (i == index)
        }
    }
}

package com.davidramos.detectormetales

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ManualPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments: List<Fragment> = listOf(
        ManualPage1Fragment(),
        ManualPage2Fragment(),
        ManualPage3Fragment(),
        ManualPage4Fragment(),
        ManualPage5Fragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}

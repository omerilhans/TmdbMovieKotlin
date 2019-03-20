package com.omerilhanli.tmdbmoviekotlin.UI.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.omerilhanli.tmdbmoviekotlin.UI.fragment.FragmentTab

class TmdbPagerAdapter(

    fragmentManager: FragmentManager,

    private val fragmentTabList: List<FragmentTab>

) : FragmentPagerAdapter(fragmentManager) {


    override fun getCount(): Int {

        return fragmentTabList.size
    }

    override fun getItem(position: Int): Fragment {

        return fragmentTabList[position]
    }
}

package com.omerilhanli.tmdbmoviekotlin.UI.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.omerilhanli.tmdbmoviekotlin.R
import com.omerilhanli.tmdbmoviekotlin.UI.BaseActivity
import com.omerilhanli.tmdbmoviekotlin.UI.adapter.TmdbPagerAdapter
import com.omerilhanli.tmdbmoviekotlin.UI.fragment.FragmentTab
import com.omerilhanli.tmdbmoviekotlin.asistant.common.Constant
import com.omerilhanli.tmdbmoviekotlin.data.interactor.Interactor
import com.omerilhanli.tmdbmoviekotlin.data.interactor.movies.*
import com.omerilhanli.tmdbmoviekotlin.data.model.Movies
import kotlinx.android.synthetic.main.activity_main.*

import java.util.ArrayList

class MainActivity : BaseActivity() {

    override val activityLayoutId: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        initAll()
    }

    private fun initAll() {

        title = Constant.TITLE

        val adapter = TmdbPagerAdapter(supportFragmentManager, createTabList())

        viewPagerContent?.adapter = adapter

        pagerIndicator.setViewPager(viewPagerContent)
    }

    private fun createTabList(): List<FragmentTab> {

        val fragmentTabList = ArrayList<FragmentTab>()

        fragmentTabList.add(create(InteractorAll(tmdbApi)))

        fragmentTabList.add(create(InteractorNowPlaying(tmdbApi)))

        fragmentTabList.add(create(InteractorPopular(tmdbApi)))

        fragmentTabList.add(create(InteractorTopRated(tmdbApi)))

        fragmentTabList.add(create(InteractorUpComing(tmdbApi)))

        return fragmentTabList
    }

    private fun create(interactor: Interactor<Movies>): FragmentTab {

        return FragmentTab.newInstance().setInteractor(interactor)
    }

    companion object {

        fun startActivityFrom(activity: Activity) {

            val intent = Intent(activity, MainActivity::class.java)

            activity.startActivity(intent)

            activity.finish()
        }
    }
}

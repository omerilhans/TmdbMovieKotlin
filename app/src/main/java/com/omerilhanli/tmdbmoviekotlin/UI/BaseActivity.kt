package com.omerilhanli.tmdbmoviekotlin.UI

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.omerilhanli.tmdbmoviekotlin.App
import com.omerilhanli.tmdbmoviekotlin.DI.component.AppComponent
import com.omerilhanli.tmdbmoviekotlin.api.TmdbApi

abstract class BaseActivity : AppCompatActivity() {

    protected var tmdbApi: TmdbApi? = null

    protected abstract val activityLayoutId: Int


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        initAll()
    }

    private fun initAll() {

        setContentView(activityLayoutId)

        inject()
    }

    private fun inject() {

        val component: AppComponent? = (application as App)?.component

        component?.inject(this)

        tmdbApi = component?.tmdbApi()
    }
}

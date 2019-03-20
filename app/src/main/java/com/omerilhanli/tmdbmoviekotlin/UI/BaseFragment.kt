package com.omerilhanli.tmdbmoviekotlin.UI

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.omerilhanli.tmdbmoviekotlin.App
import com.omerilhanli.tmdbmoviekotlin.DI.component.AppComponent
import com.omerilhanli.tmdbmoviekotlin.UI.adapter.RecylerAdapter
import com.omerilhanli.tmdbmoviekotlin.api.TmdbApi

abstract class BaseFragment : Fragment(),
    SwipeRefreshLayout.OnRefreshListener,
    RecylerAdapter.OnScroolToBottom {

    protected var tmdbApi: TmdbApi? = null

    open abstract var fragmentViewId: Int

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        inject()
    }

    private fun inject() {

        val component: AppComponent? = (activity?.application as App)?.component

        component?.inject(this)

        tmdbApi = component?.tmdbApi()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(fragmentViewId, container, false)

        return view
    }
}

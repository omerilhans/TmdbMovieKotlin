package com.omerilhanli.tmdbmoviekotlin

import android.app.Application
import com.omerilhanli.tmdbmoviekotlin.DI.component.AppComponent
import com.omerilhanli.tmdbmoviekotlin.DI.component.DaggerAppComponent
import com.omerilhanli.tmdbmoviekotlin.DI.module.ApiModule

class App : Application() {

    var component: AppComponent? = null
        private set

    override fun onCreate() {

        super.onCreate()

        component = initDaggerComponent()
    }

    private fun initDaggerComponent(): AppComponent {
        return DaggerAppComponent
            .builder().apiModule(ApiModule()).build()
    }

}
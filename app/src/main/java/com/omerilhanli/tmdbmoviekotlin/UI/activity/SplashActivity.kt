package com.omerilhanli.tmdbmoviekotlin.UI.activity

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.omerilhanli.tmdbmoviekotlin.R
import com.omerilhanli.tmdbmoviekotlin.asistant.common.Constant

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        initAll()
    }

    private fun initAll() {

        setContentView(R.layout.activity_splash)

        initDelayed()
    }

    private fun initDelayed() {

        val handler = Handler()

        handler
            .postDelayed(
                { main() },
                Constant.SPLASH_DURATION
            )
    }

    private fun main() {

        MainActivity.startActivityFrom(this)
    }
}

package com.omerilhanli.tmdbmoviekotlin.asistant.tools

import android.content.Context
import android.net.ConnectivityManager

object NetworkController {

    fun isOnline(context: Context): Boolean {

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return cm.activeNetworkInfo != null
    }
}

package com.omerilhanli.tmdbmoviekotlin.asistant.tools

import java.text.SimpleDateFormat
import java.util.Calendar

object DateFormatter {

    val releaseDate: String
        get() {

            val cal = Calendar.getInstance()

            val format1 = SimpleDateFormat("yyyy-MM-dd")

            return format1.format(cal.time)
        }
}

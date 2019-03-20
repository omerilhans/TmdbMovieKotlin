package com.omerilhanli.tmdbmoviekotlin.asistant.tools

import android.content.Context
import android.text.TextUtils
import com.omerilhanli.tmdbmoviekotlin.R
import com.omerilhanli.tmdbmoviekotlin.data.model.Genres
import com.omerilhanli.tmdbmoviekotlin.data.model.Movie
import com.omerilhanli.tmdbmoviekotlin.data.model.SpokenLanguage

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class StringFormatter(private val context: Context) {

    fun getReleaseDate(movie: Movie): String {
        if (movie.releaseDate.equals("")) {
            return "..."
        }
        val form = SimpleDateFormat("yyyy-MM-dd")
        var date: Date? = null
        try {
            date = form.parse(movie.releaseDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val postFormatter = SimpleDateFormat("dd-MMM-yyyy")
        return postFormatter.format(date)
    }

    fun getLanguages(spokenLanguages: List<SpokenLanguage>): String {
        var languages = ""
        for (i in spokenLanguages.indices) {
            val language = spokenLanguages[i]
            languages += language.name + ", "
        }

        languages = removeTrailingComma(languages)

        return if (languages.isEmpty()) "-" else languages
    }

    private fun removeTrailingComma(text: String): String {
        var text = text
        text = text.trim { it <= ' ' }
        if (text.endsWith(",")) {
            text = text.substring(0, text.length - 1)
        }
        return text
    }

    fun getGenres(genres: List<Genres>?): String {

        if (genres!!.isEmpty()) {
            return context.resources.getString(R.string.genres_error)
        }

        var genresString = ""

        for (i in genres.indices) {

            val genre = genres[i]

            genresString += genre.name + ", "
        }

        genresString = removeTrailingComma(genresString)

        return if (genresString.isEmpty()) "-" else genresString
    }

    fun getDuration(runtime: Int): String {
        return if (runtime <= 0) "-" else context.resources.getQuantityString(R.plurals.duration, runtime, runtime)
    }

    fun getOverview(overview: String): String {
        return if (TextUtils.isEmpty(overview)) "-" else overview
    }

}

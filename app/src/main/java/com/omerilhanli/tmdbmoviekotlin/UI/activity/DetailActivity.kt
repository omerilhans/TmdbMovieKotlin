package com.omerilhanli.tmdbmoviekotlin.UI.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.omerilhanli.tmdbmoviekotlin.R
import com.omerilhanli.tmdbmoviekotlin.UI.BaseActivity
import com.omerilhanli.tmdbmoviekotlin.asistant.common.Constant
import com.omerilhanli.tmdbmoviekotlin.asistant.tools.NetworkController
import com.omerilhanli.tmdbmoviekotlin.asistant.tools.StringFormatter
import com.omerilhanli.tmdbmoviekotlin.data.interactor.Interactor
import com.omerilhanli.tmdbmoviekotlin.data.interactor.images.InteractorConfiguration
import com.omerilhanli.tmdbmoviekotlin.data.interactor.movie.InteractorMovie
import com.omerilhanli.tmdbmoviekotlin.data.model.Configuration
import com.omerilhanli.tmdbmoviekotlin.data.model.Images
import com.omerilhanli.tmdbmoviekotlin.data.model.Movie
import kotlinx.android.synthetic.main.activity_detail.*
import rx.Subscriber

class DetailActivity : BaseActivity() {

    private var configurationInteractor: Interactor<Configuration>? = null

    private var movieInteractor: Interactor<Movie>? = null

    private var images: Images? = null

    private var stringFormatter: StringFormatter? = null

    private var movieId = -1

    override val activityLayoutId: Int
        get() = R.layout.activity_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initAll()
    }

    fun onMoreInfoButtonClick() {

        val url = getString(R.string.web_url) + movieId

        if (Build.VERSION.SDK_INT >= 16) {

            val builder = CustomTabsIntent.Builder()

            builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))

            val customTabsIntent = builder.build()

            customTabsIntent.launchUrl(this, Uri.parse(url))

        } else {

            val i = Intent(Intent.ACTION_VIEW)

            i.data = Uri.parse(url)

            startActivity(i)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {

            android.R.id.home -> {

                onBackPressed()

                return true
            }

            else ->

                return super.onOptionsItemSelected(item)
        }
    }

    private fun initAll() {

        stringFormatter = StringFormatter(this)

        movieInteractor = InteractorMovie(tmdbApi)

        configurationInteractor = InteractorConfiguration(tmdbApi)


        val extras = intent.extras

        if (extras != null) {

            movieId = extras.getInt(Constant.MOVIE_ID)

            val movieTitle = extras.getString(Constant.MOVIE_TITLE)

            title = movieTitle


            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            supportActionBar?.setDisplayShowHomeEnabled(true)

            fetchImages()
        }
    }

    private fun fetchImages() {

        if (!checkNetwork()) {

            return
        }

        showLoading()

        configurationInteractor!!.onRequest(0).subscribe(object : Subscriber<Configuration>() {

            override fun onCompleted() {

            }

            override fun onError(e: Throwable) {

                showError()
            }

            override fun onNext(configuration: Configuration) {

                images = configuration.images

                fetchMovies()
            }
        })
    }

    private fun fetchMovies() {

        if (checkNetwork())

            movieInteractor!!
                .onRequest(movieId)
                .subscribe(object : Subscriber<Movie>() {

                    override fun onCompleted() {

                        hideLoading()
                    }

                    override fun onError(e: Throwable) {

                        showError()
                    }

                    override fun onNext(movie: Movie) {

                        val fullImageUrl = getFullImageUrl(movie)

                        if (!fullImageUrl.isEmpty()) {

                            loadPoster(fullImageUrl)
                        }

                        genresTextView!!.text = stringFormatter!!.getGenres(movie.genres!!)

                        durationTextView!!.text = stringFormatter!!.getDuration(movie.runtime)

                        languageTextView!!.text = stringFormatter!!.getLanguages(movie.spokenLanguages!!)

                        dateTextView!!.text = stringFormatter!!.getReleaseDate(movie)

                        nameTextView!!.text = movie.title

                        overviewTextView!!.text = stringFormatter!!.getOverview(movie.overview!!)
                    }
                })
    }

    private fun loadPoster(fullImageUrl: String) {

        Glide.with(this)
            .load(fullImageUrl)
            .centerCrop()
            .crossFade()
            .listener(object : RequestListener<String, GlideDrawable> {
                override fun onException(
                    e: Exception, model: String, target: Target<GlideDrawable>,
                    isFirstResource: Boolean
                ): Boolean {

                    progress!!.visibility = View.GONE

                    return false
                }

                override fun onResourceReady(
                    resource: GlideDrawable, model: String,
                    target: Target<GlideDrawable>,
                    isFromMemoryCache: Boolean,
                    isFirstResource: Boolean
                ): Boolean {

                    progress!!.visibility = View.GONE

                    return false
                }
            })
            .into(imageView!!)
    }

    private fun getFullImageUrl(movie: Movie): String {

        val imagePath: String

        if (movie?.posterPath != null) {

            imagePath = movie.posterPath!!

        } else {

            imagePath = movie.backdropPath!!
        }

        if (!(images!!.baseUrl.isNullOrEmpty())) {

            if (images!!.posterSizes != null) {

                return if (images?.posterSizes!!.size > 4) {

                    images!!.baseUrl + images?.posterSizes?.get(4) + imagePath

                } else {

                    images!!.baseUrl + Constant.KEY_SIZE_OF_IMAGE + imagePath
                }
            }
        }

        return ""
    }

    internal fun hideLoading() {

        progress!!.visibility = View.GONE

        progressBar!!.visibility = View.GONE

        errorTextView!!.visibility = View.GONE
    }

    internal fun showLoading() {

        progress!!.visibility = View.VISIBLE

        progressBar!!.visibility = View.VISIBLE

        errorTextView!!.visibility = View.GONE
    }

    internal fun showError() {

        progressBar!!.visibility = View.GONE

        errorTextView!!.visibility = View.VISIBLE
    }

    private fun showContent(show: Boolean) {

        val visibility = if (show) View.VISIBLE else View.INVISIBLE

        container!!.visibility = visibility

        overviewHeader!!.visibility = visibility

        overviewTextView!!.visibility = visibility

        moreInfoButton!!.visibility = visibility
    }

    private fun checkNetwork(): Boolean {

        val isOnline = NetworkController.isOnline(this)

        if (!isOnline) {

            Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_SHORT).show()

            showContent(false)

            showError()
        }

        return isOnline
    }

    companion object {

        fun startActivityFrom(activity: Activity, movieId: Int, title: String) {

            val intent = Intent(activity, DetailActivity::class.java)

            intent.putExtra(Constant.MOVIE_ID, movieId)

            intent.putExtra(Constant.MOVIE_TITLE, title)

            activity.startActivity(intent)
        }
    }

}

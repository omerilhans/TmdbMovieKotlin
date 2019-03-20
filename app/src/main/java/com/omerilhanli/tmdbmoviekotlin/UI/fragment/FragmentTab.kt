package com.omerilhanli.tmdbmoviekotlin.UI.fragment

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.omerilhanli.tmdbmoviekotlin.R
import com.omerilhanli.tmdbmoviekotlin.UI.BaseFragment
import com.omerilhanli.tmdbmoviekotlin.UI.activity.DetailActivity
import com.omerilhanli.tmdbmoviekotlin.UI.adapter.RecylerAdapter
import com.omerilhanli.tmdbmoviekotlin.asistant.tools.NetworkController
import com.omerilhanli.tmdbmoviekotlin.data.interactor.Interactor
import com.omerilhanli.tmdbmoviekotlin.data.interactor.genres.InteractorGenres
import com.omerilhanli.tmdbmoviekotlin.data.interactor.images.InteractorConfiguration
import com.omerilhanli.tmdbmoviekotlin.data.interactor.search.InteractorSearch
import com.omerilhanli.tmdbmoviekotlin.data.model.*
import kotlinx.android.synthetic.main.fragment_tab.*
import rx.Subscriber

class FragmentTab : BaseFragment(), RecylerAdapter.ItemClickListener {

    private var adapter: RecylerAdapter? = null

    private var movieList: List<Movie>? = null
    private var genreList: List<Genre>? = null
    private var images: Images? = null

    private var interactor: Interactor<Movies>? = null
    private var genresInteractor: Interactor<Genres>? = null
    private var configurationInteractor: Interactor<Configuration>? = null
    private var searchInteractor: Interactor<Movies>? = null

    private var page = 1

    override var fragmentViewId: Int = 0
        get() = R.layout.fragment_tab

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        genresInteractor = InteractorGenres(tmdbApi)

        configurationInteractor = InteractorConfiguration(tmdbApi)

        searchInteractor = InteractorSearch(tmdbApi)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        initAll()
    }

    private fun initAll() {

        setupContentView()

        fetchMovies(true)

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {

                if (query.length > 3) {

                    if (!checkNetwork()) {

                        return false
                    }

                    if (swipeRefreshLayout != null && !swipeRefreshLayout!!.isRefreshing) {

                        swipeRefreshLayout!!.isRefreshing = true
                    }

                    searchInteractor?.onRequest(query)?.subscribe(object : Subscriber<Movies>() {

                        override fun onCompleted() {
                            Log.e(tag, "onCompleted")
                        }

                        override fun onError(e: Throwable) {
                            Log.e(tag, "onError")
                        }

                        override fun onNext(movies: Movies) {

                            fetchGenreList(true, movies)
                        }
                    })
                }

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                return false
            }
        })
    }

    private fun setupContentView() {

        swipeRefreshLayout?.setOnRefreshListener(this)

        val mLayoutManager = LinearLayoutManager(context)

        recyclerView?.layoutManager = mLayoutManager
    }

    private fun fetchMovies(isInitial: Boolean) {

        if (!checkNetwork()) {

            return
        }

        if (swipeRefreshLayout != null && !swipeRefreshLayout!!.isRefreshing) {

            swipeRefreshLayout!!.isRefreshing = true
        }


        interactor?.onRequest(page)!!.subscribe(object : Subscriber<Movies>() {

            override fun onCompleted() {
                Log.e(tag, "onCompleted")
            }

            override fun onError(e: Throwable) {
                Log.e(tag, "onError")
            }

            override fun onNext(movies: Movies) {

                Log.e(tag, "onNext")

                fetchGenreList(isInitial, movies)
            }
        })
    }

    private fun fetchGenreList(isInitial: Boolean, movies: Movies) {

        if (checkNetwork())

            genresInteractor?.onRequest(page)?.subscribe(object : Subscriber<Genres>() {

                override fun onCompleted() {
                    Log.e(tag, "onCompleted")
                }

                override fun onError(e: Throwable) {
                    Log.e(tag, "onError")
                }

                override fun onNext(genres: Genres) {

                    genreList = genres.genres

                    fetchImages(isInitial, movies)
                }
            })
    }

    private fun fetchImages(isInitial: Boolean, movies: Movies) {

        if (checkNetwork())

            configurationInteractor?.onRequest(page)?.subscribe(object : Subscriber<Configuration>() {

                override fun onCompleted() {
                    Log.e(tag, "onCompleted")
                }

                override fun onError(e: Throwable) {
                    Log.e(tag, "onError")
                }

                override fun onNext(configuration: Configuration) {

                    images = configuration.images

                    if (isInitial) {

                        movieList = movies.movies

                        prepareRecycler()

                    } else {

                        adapter?.add(movies.movies!!)
                    }

                    recyclerView?.visibility = View.VISIBLE

                    if (swipeRefreshLayout!!.isRefreshing) {

                        swipeRefreshLayout!!.isRefreshing = false
                    }
                }
            })
    }

    private fun prepareRecycler() {

        val manager = LinearLayoutManager(context)

        manager.orientation = LinearLayoutManager.VERTICAL

        adapter = RecylerAdapter(context!!, this, this, genreList, images)

        adapter?.add(movieList!!)

        recyclerView?.layoutManager = manager

        recyclerView?.adapter = adapter
    }

    private fun checkNetwork(): Boolean {

        val isOnline = NetworkController.isOnline(context!!)

        if (!isOnline) {

            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
        }

        return isOnline
    }

    override fun onRefresh() {

        page = 1 // reset

        recyclerView?.visibility = View.GONE

        fetchMovies(true)
    }

    override fun onScrollToBottom(visible: Boolean) {

        if (visible) {

            page++

            fetchMovies(false)
        }
    }

    fun setInteractor(interactor0: Interactor<Movies>): FragmentTab {

        this.interactor = interactor0

        return this
    }


    override fun onItemClick(movieId: Int, title: String?) {

        DetailActivity.startActivityFrom(activity!!, movieId, title!!)
    }

    companion object {

        fun newInstance(): FragmentTab {

            return FragmentTab()
        }
    }
}
package com.omerilhanli.tmdbmoviekotlin.UI.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.omerilhanli.tmdbmoviekotlin.R
import com.omerilhanli.tmdbmoviekotlin.asistant.common.Constant
import com.omerilhanli.tmdbmoviekotlin.asistant.tools.StringFormatter
import com.omerilhanli.tmdbmoviekotlin.data.model.Genre
import com.omerilhanli.tmdbmoviekotlin.data.model.Images
import com.omerilhanli.tmdbmoviekotlin.data.model.Movie
import org.apache.commons.lang3.StringUtils

import java.util.ArrayList

class RecylerAdapter(
    private val context: Context,
    private val onScroolToBottom: OnScroolToBottom,
    private val itemClickListener: ItemClickListener,
    private var genreList: List<Genre>?,
    private var images: Images?
) : RecyclerView.Adapter<RecylerAdapter.VH>() {

    private val stringFormatter: StringFormatter

    private val movieList: MutableList<Movie>

    init {

        movieList = ArrayList()

        stringFormatter = StringFormatter(context)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): VH {

        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_view, viewGroup, false)

        return VH(view)
    }

    fun getMovieList(): List<Movie> {

        return movieList
    }

    fun add(movies: List<Movie>) {

        movieList.addAll(movies)

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val movie = movieList[position]

        val fullImageUrl = getFullImageUrl(movie)

        if (!fullImageUrl.isEmpty()) {

            Glide.with(context)
                .load(fullImageUrl)
                .centerCrop()
                .crossFade()
                .into(holder.imageView!!)
        }


        holder.releaseTextView!!.text = stringFormatter.getReleaseDate(movie)

        holder.titleTextView!!.text = movie.title

        holder.genreTextView!!.text = getMainGenres(movie)

        holder.itemView.setOnClickListener { itemClickListener.onItemClick(movie.id, movie.title) }

        if (position == movieList.size - 1) {

            onScroolToBottom.onScrollToBottom(true)

        } else {

            onScroolToBottom.onScrollToBottom(false)
        }
    }

    private fun getMainGenres(movie: Movie): String {

        if (movie.genresId!!.isEmpty()) {

            return context.resources.getString(R.string.genres_error)
        }

        val genresString = ArrayList<String>()

        for ((id1, name) in genreList!!) {

            for (id in movie.genresId!!) {

                if (id1!!.equals(id)) {

                    genresString.add(name!!)
                }
            }
        }

        return StringUtils.join(genresString, ", ")
    }

    private fun getFullImageUrl(movie: Movie): String {

        val imagePath: String?

        if(!movie.posterPath.isNullOrEmpty()){

            imagePath = movie.posterPath

        } else {

            imagePath = movie.backdropPath
        }

        if (!images?.baseUrl.isNullOrEmpty()) {

            if (!images?.posterSizes.isNullOrEmpty()) {

                return if (images?.posterSizes?.size ?: 0 > 4) {

                    images!!.baseUrl + images!!.posterSizes!![4] + imagePath

                } else {

                    images!!.baseUrl + Constant.KEY_SIZE_OF_IMAGE + imagePath
                }
            }
        }
        return ""
    }

    fun clear() {
        movieList.clear()
    }

    fun addAll(movies: List<Movie>) {
        this.movieList.addAll(movies)
    }

    fun setImages(images: Images) {
        this.images = images
    }

    fun setGenres(genres: List<Genre>) {
        this.genreList = genres
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageView: ImageView? = null

        var releaseTextView: TextView? = null

        var popularityContainer: FrameLayout? = null

        var titleTextView: TextView? = null

        var genreTextView: TextView? = null

        init {
            imageView = itemView.findViewById(R.id.imageView)
            releaseTextView = itemView.findViewById(R.id.releaseTextView)
            popularityContainer = itemView.findViewById(R.id.popularityContainer)
            titleTextView = itemView.findViewById(R.id.titleTextView)
            genreTextView = itemView.findViewById(R.id.genreTextView)
        }
    }

    interface OnScroolToBottom {

        fun onScrollToBottom(visible: Boolean)
    }

    interface ItemClickListener {

        fun onItemClick(movieId: Int, title: String?)

    }
}

package com.abdulaziz.movieplus.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdulaziz.movieplus.data.models.trending.MovieSimple
import com.abdulaziz.movieplus.databinding.ItemPagerBinding
import com.squareup.picasso.Picasso

class ViewPagerAdapter(private val listener: OnPageClickListener) : RecyclerView.Adapter<ViewPagerAdapter.PagerVH>() {

    private var list: List<MovieSimple> = arrayListOf()

    inner class PagerVH(private val itemBinding: ItemPagerBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(movieSimple: MovieSimple) {
            itemBinding.apply {
                Picasso.get().load("https://image.tmdb.org/t/p/w500/${movieSimple.poster_path}")
                    .into(itemBinding.imageView)

                titleTv.text = movieSimple.original_title
                ratingTv.text = "Rating: ${movieSimple.vote_average}/10"
                root.setOnClickListener {
                    listener.onPageClick(movieSimple.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH {
        return PagerVH(ItemPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PagerVH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<MovieSimple>) {
        this.list = list
        notifyDataSetChanged()
    }

    interface OnPageClickListener {
        fun onPageClick(movie_id:Int)
    }
}
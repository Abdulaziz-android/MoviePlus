package com.abdulaziz.movieplus.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdulaziz.movieplus.data.models.trending.MovieSimple
import com.abdulaziz.movieplus.databinding.ItemSearchBinding
import com.squareup.picasso.Picasso

class SearchAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<SearchAdapter.SVH>(){

    private var list:List<MovieSimple> = arrayListOf()

    inner class SVH(private val itemBinding: ItemSearchBinding):RecyclerView.ViewHolder(itemBinding.root){
        @SuppressLint("SetTextI18n")
        fun onBind(movie:MovieSimple) {
            itemBinding.apply {
                titleTv.text = movie.original_title
                ratingTv.text = "Rating: ${movie.vote_average}"
                Picasso.get().load("https://image.tmdb.org/t/p/w500/" + movie.poster_path)
                    .into(imageView)

                root.setOnClickListener {
                    listener.onItemClicked(movie.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SVH {
        return SVH(ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SVH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = if (list.size>14) 14 else list.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<MovieSimple>){
        this.list = list
        notifyDataSetChanged()
    }

    interface OnItemClickListener{
        fun onItemClicked(movie_id:Int)
    }
}
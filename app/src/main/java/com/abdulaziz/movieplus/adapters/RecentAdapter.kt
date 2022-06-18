package com.abdulaziz.movieplus.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdulaziz.movieplus.data.models.movie.Movie
import com.abdulaziz.movieplus.databinding.ItemRecentBinding
import com.squareup.picasso.Picasso

class RecentAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<RecentAdapter.RVH>(){

    private var list:List<Movie> = arrayListOf()

    inner class RVH(private val itemBinding: ItemRecentBinding):RecyclerView.ViewHolder(itemBinding.root){
        @SuppressLint("SetTextI18n")
        fun onBind(movie:Movie) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVH {
        return RVH(ItemRecentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RVH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<Movie>){
        this.list = list
        notifyDataSetChanged()
    }

    interface OnItemClickListener{
        fun onItemClicked(movie_id:Int)
    }
}
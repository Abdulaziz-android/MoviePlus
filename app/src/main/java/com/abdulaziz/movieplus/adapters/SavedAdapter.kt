package com.abdulaziz.movieplus.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdulaziz.movieplus.data.models.movie.Movie
import com.abdulaziz.movieplus.databinding.ItemSavedBinding
import com.squareup.picasso.Picasso

class SavedAdapter(val listener: OnItemClickListener) : RecyclerView.Adapter<SavedAdapter.SVH>(){

    private var list:ArrayList<Movie> = arrayListOf()

    inner class SVH(private val itemBinding: ItemSavedBinding):RecyclerView.ViewHolder(itemBinding.root){
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun onBind(movie:Movie) {
            itemBinding.apply {
                titleTv.text = movie.original_title
                ratingTv.text = "Rating: ${movie.vote_average}"
                Picasso.get().load("https://image.tmdb.org/t/p/w500/" + movie.poster_path)
                    .into(imageView)

                root.setOnClickListener {
                    listener.onItemClicked(movie.id)
                }

                saveIv.setOnClickListener {
                    list.remove(movie)
                    listener.onSaveClicked(movie)
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SVH {
        return SVH(ItemSavedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SVH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<Movie>){
        this.list = list as ArrayList<Movie>
        notifyDataSetChanged()
    }

    interface OnItemClickListener{
        fun onItemClicked(movie_id:Int)
        fun onSaveClicked(movie: Movie)
    }
}
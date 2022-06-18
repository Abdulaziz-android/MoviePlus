package com.abdulaziz.movieplus.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdulaziz.movieplus.data.models.images.Backdrop
import com.abdulaziz.movieplus.databinding.ItemImageBinding
import com.squareup.picasso.Picasso

class ImageAdapter(val list: List<Backdrop>) : RecyclerView.Adapter<ImageAdapter.IVH>(){


    inner class IVH(private val itemBinding: ItemImageBinding):RecyclerView.ViewHolder(itemBinding.root){
        fun onBind(backdrop: Backdrop){
            Picasso.get().load("https://image.tmdb.org/t/p/w500/"+backdrop.file_path).into(itemBinding.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IVH {
        return IVH(ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: IVH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = if (list.size>12) 12 else list.size

}
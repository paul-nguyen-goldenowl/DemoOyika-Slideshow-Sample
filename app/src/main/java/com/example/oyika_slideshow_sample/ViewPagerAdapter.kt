package com.example.oyika_slideshow_sample

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.oyika_slideshow_sample.databinding.ItemSlideShowBinding

class SlideShowAdapter(private val urls: List<String>, private val  listener: ClickListener) :
    RecyclerView.Adapter<SlideShowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideShowViewHolder {
        return SlideShowViewHolder(
            ItemSlideShowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = urls.size

    override fun onBindViewHolder(holder: SlideShowViewHolder, position: Int) {
        val url = urls[position]
        holder.bind(url, listener)
    }
}

class SlideShowViewHolder(binding: ItemSlideShowBinding) : RecyclerView.ViewHolder(binding.root) {
    private val imageView: ImageView = binding.imageView

    fun bind(url: String, listener: ClickListener) {
        Glide.with(itemView)
            .load(url)
            .centerCrop()
            .into(imageView)

        imageView.setOnClickListener {
            listener.onClick()
        }
    }
}
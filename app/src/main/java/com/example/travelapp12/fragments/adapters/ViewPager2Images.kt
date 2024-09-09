package com.example.travelapp12.fragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelapp12.databinding.ViewImageSightsBinding
import com.example.travelapp12.fragments.data.Sights

class ViewPager2Images(private val imageList: List<String>) :
    RecyclerView.Adapter<ViewPager2Images.ViewPager2ImageViewHolder>() {

    inner class ViewPager2ImageViewHolder(val binding: ViewImageSightsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imagePath: String) {
            Glide.with(binding.imageSightDetail)
                .load(imagePath)
                .into(binding.imageSightDetail)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2ImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewImageSightsBinding.inflate(inflater, parent, false)
        return ViewPager2ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPager2ImageViewHolder, position: Int) {
        val imagePath = imageList[position]
        holder.bind(imagePath)
    }

    override fun getItemCount() = imageList.size
}



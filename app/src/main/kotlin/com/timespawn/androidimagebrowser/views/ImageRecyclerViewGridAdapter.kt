package com.timespawn.androidimagebrowser.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.timespawn.androidimagebrowser.R
import com.timespawn.androidimagebrowser.models.ImageData

class ImageRecyclerViewGridAdapter(private val imageDatas: ArrayList<ImageData>) :
    RecyclerView.Adapter<ImageRecyclerViewGridAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val previewImage: ImageView = view.findViewById(R.id.previewImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_grid_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = imageDatas[position]
        holder.previewImage.load(data.previewUrl) {
            crossfade(true)
        }
    }

    override fun getItemCount(): Int {
        return imageDatas.size
    }
}
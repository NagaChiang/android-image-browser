package com.timespawn.androidimagebrowser.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.timespawn.androidimagebrowser.R
import com.timespawn.androidimagebrowser.models.ImageData

class ImageRecyclerViewAdapter(private val imageDatas: ArrayList<ImageData>) :
    RecyclerView.Adapter<ImageRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val previewImage: ImageView = view.findViewById(R.id.previewImage)
        val viewText: TextView = view.findViewById(R.id.viewText)
        val likeText: TextView = view.findViewById(R.id.likeText)
        val downloadText: TextView = view.findViewById(R.id.downloadText)
        val commentText: TextView = view.findViewById(R.id.commentText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = imageDatas[position]

        holder.previewImage.load(data.previewUrl) {
            crossfade(true)
        }

        holder.viewText.text = data.viewCount.toString()
        holder.likeText.text = data.likeCount.toString()
        holder.downloadText.text = data.downloadCount.toString()
        holder.commentText.text = data.commentCount.toString()
    }

    override fun getItemCount(): Int {
        return imageDatas.size
    }
}
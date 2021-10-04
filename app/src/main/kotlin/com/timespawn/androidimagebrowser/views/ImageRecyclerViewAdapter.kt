package com.timespawn.androidimagebrowser.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.timespawn.androidimagebrowser.R
import com.timespawn.androidimagebrowser.models.ImageData

class ImageRecyclerViewAdapter(private val imageDatas: ArrayList<ImageData>) :
    RecyclerView.Adapter<ImageRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = imageDatas[position].id
    }

    override fun getItemCount(): Int {
        return imageDatas.size
    }
}
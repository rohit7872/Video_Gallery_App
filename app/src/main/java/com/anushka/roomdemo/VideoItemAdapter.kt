package com.anushka.roomdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class VideoItemAdapter(
    private var videos: List<VideoEntity>,
    private val onItemClick: (Long) -> Unit
) : RecyclerView.Adapter<VideoItemAdapter.ViewHolder>() {

    // ViewHolder and other methods...

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = videos[position]
        holder.bind(video)
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    fun updateVideoList(newVideos: List<VideoEntity>) {
        videos = newVideos
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val videoThumbnail: ImageView = itemView.findViewById(R.id.videoThumbnail)
        private val videoTitle: TextView = itemView.findViewById(R.id.videoTitle)

        fun bind(video: VideoEntity) {
            // Load video details into UI elements
            // You may use a library like Glide for efficient image loading
            videoTitle.text = "Vidio"

            // Set an OnClickListener to handle item clicks
            itemView.setOnClickListener {
                onItemClick.invoke(video.id)
            }
        }
    }
}


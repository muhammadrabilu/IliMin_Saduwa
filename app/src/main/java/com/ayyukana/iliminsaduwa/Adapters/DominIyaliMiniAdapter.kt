package com.ayyukana.iliminsaduwa.Adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayyukana.iliminsaduwa.Activity.DominIyaliVideoPlayerActivity
import com.ayyukana.iliminsaduwa.R
import com.ayyukana.iliminsaduwa.`interface`.OnItemClickListener
import com.ayyukana.iliminsaduwa.model.DominIyaliModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.mini_layout.view.*

class DominIyaliMiniAdapter(
    val videos: ArrayList<DominIyaliModel>,
    val onItemClickListener: DominIyaliVideoPlayerActivity
) :
    RecyclerView.Adapter<DominIyaliMiniAdapter.myView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mini_layout, parent, false)
        return myView(view, onItemClickListener)
    }

    override fun onBindViewHolder(holder: myView, position: Int) {
        val video = videos[position]

        holder.itemView.mVideo_title.text = video.title

        Glide.with(holder.itemView.mVideo_thumbnail)
            .load(Uri.parse(video.videoURL))
            .centerCrop()
            .into(holder.itemView.mVideo_thumbnail)
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    inner class myView(itemView: View, val OnItemClickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                OnItemClickListener.onItemClick(adapterPosition)
            }
        }
    }
}
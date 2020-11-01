package com.ayyukana.iliminsaduwa.Adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayyukana.iliminsaduwa.R
import com.ayyukana.iliminsaduwa.Respond.Respond
import com.ayyukana.iliminsaduwa.Respond.Snippet
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.mini_layout.view.*

class YoutubeMiniAdapter(
    val respond: Respond,
    val mOnItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<YoutubeMiniAdapter.myViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mini_layout, parent, false)

        return myViewHolder(view, mOnItemClickListener)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val video = respond.items[position]


        holder.itemView.mVideo_title.text = video.snippet.title
        holder.snippet = video.snippet

        Glide.with(holder.itemView)
            .load(Uri.parse(video.snippet.thumbnails.high.url))
            .centerCrop()
            .into(holder.itemView.mVideo_thumbnail)
    }

    override fun getItemCount(): Int {
        return respond.items.size
    }

    inner class myViewHolder(
        itemView: View,
        val onItemClickListener: OnItemClickListener,
        var snippet: Snippet? = null
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            onItemClickListener.onClick(adapterPosition, snippet)
        }
    }

    interface OnItemClickListener {
        fun onClick(position: Int, snippet: Snippet?)
    }
}
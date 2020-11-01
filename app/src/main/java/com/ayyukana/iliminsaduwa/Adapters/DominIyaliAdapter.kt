package com.ayyukana.iliminsaduwa.Adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayyukana.iliminsaduwa.R
import com.ayyukana.iliminsaduwa.`interface`.OnEditbtnClickListener
import com.ayyukana.iliminsaduwa.`interface`.OnItemClickListener
import com.ayyukana.iliminsaduwa.model.DominIyaliModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.custom_layout.view.*

class DominIyaliAdapter(
    val context: Context,
    val dominIyalimodel: ArrayList<DominIyaliModel>,
    val mOnItemClickListener: OnItemClickListener,
    val onEditbtnClickListener: OnEditbtnClickListener
) :
    RecyclerView.Adapter<DominIyaliAdapter.viewHolder>() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.custom_layout, parent, false)
        return viewHolder(view, mOnItemClickListener, onEditbtnClickListener)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val video = dominIyalimodel[position]

        holder.itemView.video_title.text = video.title

        Glide.with(holder.itemView.video_thumbnail)
            .load(Uri.parse(video.videoURL))
            .into(holder.itemView.video_thumbnail)
    }

    override fun getItemCount(): Int {
        return dominIyalimodel.size
    }

    inner class viewHolder(
        itemView: View,
        private val onItemClickListener: OnItemClickListener,
        val onEditbtnClickListener: OnEditbtnClickListener
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)

            if (firebaseAuth.currentUser?.email == "ayyukana@gmail.com") {
                itemView.btnEdit.visibility = View.VISIBLE
                itemView.btnEdit.setOnClickListener {
                    onEditbtnClickListener.onUpdateBtnClick(adapterPosition)
                }
            }

        }

        override fun onClick(p0: View?) {
            onItemClickListener.onItemClick(adapterPosition)
        }

    }

}
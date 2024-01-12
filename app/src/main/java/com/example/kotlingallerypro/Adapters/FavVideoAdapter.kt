package com.example.kotlingallerypro.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.kotlingallerypro.Interface.AlbumClickInterface
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.modelclass.AlbumImageSliderModel
import com.example.kotlingallerypro.modelclass.Videomodel
import com.example.kotlingallerypro.modelclass.VideosliderModel

class FavVideoAdapter(val context: Context, private val albumvdopic: ArrayList<Videomodel>, val clickListener: AlbumClickInterface)
    : RecyclerView.Adapter<FavVideoAdapter.PictuViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, ): PictuViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.album_folder, parent, false)
        return PictuViewHolder(view)
    }

    override fun onBindViewHolder(holder:PictuViewHolder, position: Int) {
        val  albumVModel: Videomodel = albumvdopic[position]

        if (albumVModel != null){
            Glide.with(context).load(albumVModel.path).centerCrop().into(holder.imageView)

            holder.tvName.text = albumVModel.getTitle()
            holder.itemView.setOnClickListener{
                clickListener.OnVideoClick ( holder,  albumvdopic , position)

            }

        }
    }

    override fun getItemCount(): Int {
        return albumvdopic.size

    }
    class PictuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.grid)
        var tvName: TextView = itemView.findViewById(R.id.name_text)

    }


}
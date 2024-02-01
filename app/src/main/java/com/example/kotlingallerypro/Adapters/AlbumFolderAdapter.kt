package com.example.kotlingallerypro.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kotlingallerypro.Activity.AlbumshowActivity
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.modelclass.AlbumDetail
class AlbumFolderAdapter(album: ArrayList<AlbumDetail>, context: Context) :
        RecyclerView.Adapter<AlbumFolderAdapter.AlbumViewHolder>() {
        private val album: ArrayList<AlbumDetail>
        private val context: Context
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):AlbumViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view: View = layoutInflater.inflate(R.layout.album_folder, parent, false)
            return AlbumViewHolder(view)
        }

        override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
            val albumModel: AlbumDetail = album[position]


            Glide.with(context).load(albumModel.firstImage).apply(RequestOptions().centerCrop())
                .into(holder.imageView)
            val folderName: String = albumModel.folderName
            val folderTotalImage = "" + albumModel.numberOfImage
            val path: String = albumModel.path
            holder.albumName.text = folderName
            holder.albumName.isSelected = true
            holder.totalImage.text = folderTotalImage
            holder.itemView.setOnClickListener{v->
                var intent = Intent(context, AlbumshowActivity::class.java)
                intent.putExtra("folderPath", path)
                intent.putExtra("folderName", folderName)
                context.startActivity(intent)
            }

        }


    override fun getItemCount(): Int {
            return album.size
        }

        init {
            this.album = album
            this.context = context
        }

    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var albumName: TextView
        var totalImage: TextView

        init {
            imageView = itemView.findViewById(R.id.grid)
            albumName = itemView.findViewById(R.id.name_text)
            totalImage = itemView.findViewById(R.id.count_text)
        }
    }
    }





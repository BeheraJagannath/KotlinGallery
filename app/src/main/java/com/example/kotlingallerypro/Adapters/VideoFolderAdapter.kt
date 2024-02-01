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
import com.example.kotlingallerypro.Activity.VideoshowActivity
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.modelclass.VideoFolderModel
import java.io.File
class VideoFolderAdapter(
    val context: Context?, private val album: ArrayList<String>?,
    private val videoFolderList: ArrayList<VideoFolderModel>?,
)
    : RecyclerView.Adapter<VideoFolderAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.album_folder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val file = File(videoFolderList!![position].firstPic)

        Glide.with(context!!)
            .load(file.path)
            .centerCrop()
            .into(holder.imageView)

        holder.albumName.text = videoFolderList[position].folderName
        holder.albumName.isSelected = true
        holder.totalImage.text = videoFolderList[position].numberOfVideo.toString()
        holder.itemView.setOnClickListener{

            val intent = Intent(context, VideoshowActivity::class.java)
            intent.putExtra("folderName",videoFolderList.get(position).path)
            intent.putExtra("name", holder.albumName.getText().toString())
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return album?.size!!
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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





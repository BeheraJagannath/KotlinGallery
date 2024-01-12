package com.example.kotlingallerypro.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlingallerypro.Activity.AllimageSliderActivity
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.modelclass.AllImagesModel
import com.example.kotlingallerypro.modelclass.ImageSliderModel
import java.io.File

class ImageAdapter ( var allImagesModelList: List<AllImagesModel>, var imageList: List<String> ,var context: Context ) :
    RecyclerView.Adapter<ImageAdapter.MyClassView?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyClassView {
        val v1: View =
            LayoutInflater.from(parent.context).inflate(R.layout.image_files, parent, false)
        return MyClassView(v1)
    }

    override fun onBindViewHolder(holder: MyClassView, @SuppressLint("RecyclerView") position: Int) {
        val imageItem: AllImagesModel = allImagesModelList[position]
        val date = imageList[position]

        Glide .with( context ). load(File(imageItem.path)).fitCenter().centerCrop().into(holder.img_images)
        val imageSliderModel = ImageSliderModel(allImagesModelList, position)
        Toast.makeText(context, imageSliderModel.toString(), Toast.LENGTH_SHORT).show()
        holder.itemView.setOnClickListener {

            val intent = Intent(context, AllimageSliderActivity::class.java)
            intent.putExtra("key", imageSliderModel)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class MyClassView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_images: ImageView

        init {
            img_images = itemView.findViewById(R.id.image)
        }
    }

    init {
        this.allImagesModelList = allImagesModelList
        this.imageList = imageList
        this.context = context
    }
}



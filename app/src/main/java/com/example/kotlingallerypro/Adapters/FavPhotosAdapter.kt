package com.example.kotlingallerypro.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.*
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlingallerypro.Activity.AlbumSliderActivity
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.modelclass.AlbumImageSliderModel
import com.example.kotlingallerypro.modelclass.AlbumPictureModel
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*

//class FavPhotosAdapter {
//}
class FavPhotosAdapter(pictureList: ArrayList<AlbumPictureModel>, context: Context ) :
    RecyclerView.Adapter<FavPhotosAdapter.PictViewHolder?>() {
    private val pictureList: List<AlbumPictureModel>
    private val context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.image_files, parent, false)
        return PictViewHolder(view)
    }

    override fun onBindViewHolder(holder: PictViewHolder, @SuppressLint("RecyclerView") position: Int, ) {
        val pictureModel: AlbumPictureModel = pictureList[position]

//        Glide.with(context).load(pictureModel.getPicturePath()).apply(new RequestOptions().centerCrop()).into(holder.imageView);
        Picasso.get().load(File(pictureModel.getPicturePath())).fit()
            .centerCrop()
            .into(holder.imageView)
        ViewCompat.setTransitionName(holder.imageView, position.toString() + "_image")
        holder.itemView.setOnClickListener{
            val albumImageSliderModel = AlbumImageSliderModel(pictureList, position)
            @SuppressLint("NewApi", "LocalSuppress") val intent = Intent(context, AlbumSliderActivity::class.java)
            intent.putExtra("key", albumImageSliderModel)
            context.startActivity(intent)

        }


    }

    override fun getItemCount(): Int {
        return pictureList.size
    }


    inner class PictViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var relativeLayout: RelativeLayout
        var imageView: ImageView
        var tickk: ImageView
        var mview: ImageView
        var frameLayout: FrameLayout

        init {
            imageView = view.findViewById(R.id.image)
            relativeLayout = view.findViewById(R.id.item_selected)
            tickk = view.findViewById(R.id.tickk)
            mview = view.findViewById(R.id.view)
            frameLayout = view.findViewById(R.id.item_image)
        }
    }

    init {
        this.pictureList = pictureList
        this.context = context

    }
}


package com.example.kotlingallerypro.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlingallerypro.Interface.SelectedVideoItem
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.modelclass.AlbumPictureModel

class AlbumshowAdapter(private var pictureList: ArrayList<AlbumPictureModel>, private var context: Context, private var selectedVideoItem: SelectedVideoItem) :

    RecyclerView.Adapter<AlbumshowAdapter.PictViewHolder>(){
        companion object{
             lateinit var selected_items: SparseBooleanArray
             var current_selected_idx = -1
             var deleteImageMap = HashMap<Int, String>()


        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.image_files, parent, false)
        return PictViewHolder(view)
    }

    override fun onBindViewHolder(holder: PictViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val pictureModel: AlbumPictureModel = pictureList[position]

//        Glide.with(context).load(pictureModel.getPicturePath()).apply(new RequestOptions().centerCrop()).into(holder.imageView);
        if (pictureModel != null){
            Glide.with(context)
                .load(pictureModel.picturePath)
                .fitCenter()
                .centerCrop()
                .into(holder.imageView)

        }

        ViewCompat.setTransitionName(holder.imageView, position.toString() + "_image")
        holder.itemView.setOnClickListener {
            selectedVideoItem.onPicClicked(holder, position, pictureList)
        }
        holder.itemView.setOnLongClickListener { v ->
            selectedVideoItem.onItemLongClick(v, pictureModel, position)
             true
        }
        toggleCheckedIcon(holder, position)
        displayImage(holder, pictureModel, position)
    }

    private fun displayImage(holder: AlbumshowAdapter.PictViewHolder, pictureModel: AlbumPictureModel, position: Int) {
        if (pictureModel.getImageUri() != null) {
            holder.imageView.colorFilter = null
            holder.relativeLayout.visibility = View.GONE
        } else {
            holder.frameLayout.visibility = View.VISIBLE
        }

    }

    private fun toggleCheckedIcon(holder: PictViewHolder, position: Int) {
        if (selected_items.get(position, false)) {

            holder.frameLayout.visibility = View.GONE
            holder.relativeLayout.visibility = View.VISIBLE
            holder.mview.visibility = View.VISIBLE
            holder.relativeLayout.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.fade_in))
            holder.mview.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
            if (current_selected_idx == position) resetCurrentIndex()

        } else {
            holder.relativeLayout.visibility = View.GONE
            holder.relativeLayout.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.fade_out))
            holder.mview.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
            holder.mview.visibility = View.GONE
            holder.frameLayout.visibility = View.VISIBLE
            if (current_selected_idx == position) resetCurrentIndex()
        }

    }
    private fun resetCurrentIndex() {
        current_selected_idx = -1
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
            this. selectedVideoItem= selectedVideoItem
            selected_items = SparseBooleanArray()

    }

    fun clearSelections() {
        selected_items.clear()
        notifyDataSetChanged()
    }

    fun clearList() {
        deleteImageMap.clear()
    }
    fun getSelectedItemCount(): Int {
        return selected_items.size()
    }
    fun getDeleteItems(): HashMap<Int, String> {
        return deleteImageMap
    }
    fun toggleSelection(pos: Int) {
        current_selected_idx = pos
        if (selected_items[pos, false]) {
            selected_items.delete(pos)
            deleteImageMap.remove(pos)
        } else {
            selected_items.put(pos, true)
            deleteImageMap.put(pos, pictureList[pos].picturePath)
            Log.d("add", "toggleCheckedIcon: $pos")
        }
        notifyItemChanged(pos)
    }

    fun removeData(position: Int) {
        pictureList.removeAt(position)
        resetCurrentIndex()
        notifyItemRemoved(position)
    }


}






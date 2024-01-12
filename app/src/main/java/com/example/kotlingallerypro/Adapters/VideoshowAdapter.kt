package com.example.kotlingallerypro.Adapters

import android.app.Activity
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlingallerypro.Interface.VideoClicklistener
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.modelclass.Videomodel
import java.io.File

 class VideoshowAdapter(
    var videosList: ArrayList<Videomodel> = ArrayList<Videomodel>(),
    var activity: Activity, private var videoclicklistener: VideoClicklistener, ): RecyclerView.Adapter<VideoshowAdapter.ViewViewHolders> (){

    lateinit var selected_items: SparseBooleanArray
    var current_selected_idx = -1
    var deleteImageMap = HashMap<Int, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoshowAdapter.ViewViewHolders {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate( R.layout.video_view, parent, false )
        return ViewViewHolders(view)
    }

    override fun onBindViewHolder(holder: VideoshowAdapter.ViewViewHolders, position: Int) {
        val file: File = File(videosList.get(position).path)
        val videoModel: Videomodel = videosList[position]

        Glide.with(holder.itemView.getContext())
            .load(file.path)
            .fitCenter()
            .centerCrop()
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            videoclicklistener.OnVideoClick (holder ,videosList, position)



        }
        holder.itemView.setOnLongClickListener { v ->
            videoclicklistener.OnVideoLongClick(v, file, position)
            true
        }
        toggleCheckedIcon(holder, position)
        displayImage(holder, videoModel, position)


    }

    private fun displayImage(holder: ViewViewHolders, videomodel: Videomodel, position: Int) {
        if (videosList[position].path!= null) {
//            holder.imageView.colorFilter = null
            holder.imageView.visibility = View.VISIBLE
        } else {
            holder.imageView.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return videosList.size
    }

    private fun toggleCheckedIcon(holder:VideoshowAdapter. ViewViewHolders, position: Int) {
        if (selected_items.get(position, false)) {

            holder.frameLayout.visibility = View.VISIBLE
            holder.relativeLayout.visibility = View.VISIBLE
            holder.mview.visibility = View.VISIBLE
            holder.relativeLayout.startAnimation(AnimationUtils.loadAnimation(activity,
                R.anim.fade_in))
            holder.mview.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_in))
            if (current_selected_idx == position) resetCurrentIndex()

        } else {

            holder.mview.visibility = View.GONE
            holder.frameLayout.visibility = View.VISIBLE
            holder.relativeLayout.visibility = View.GONE
            holder.relativeLayout.startAnimation(AnimationUtils.loadAnimation(activity,
                R.anim.fade_out))
            holder.mview.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_out))

            if (current_selected_idx == position) resetCurrentIndex()
        }

    }
    private fun resetCurrentIndex() {
        current_selected_idx = -1
    }


    inner class ViewViewHolders(view: View) : RecyclerView.ViewHolder(view) {
        var relativeLayout: RelativeLayout
        var imageView: ImageView
        var tickk: ImageView
        var mview: View
        var frameLayout: FrameLayout

        init {
            imageView = view.findViewById(R.id.image_view)
            relativeLayout = view.findViewById(R.id.mul_selection)
            tickk = view.findViewById(R.id.tickk)
            mview = view.findViewById(R.id.view)
            frameLayout = view.findViewById(R.id.item_video)
        }
    }
    init {
        this.videosList = videosList
        this.activity = activity
        this. videoclicklistener= videoclicklistener
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
                deleteImageMap.put(pos, videosList[pos].path)
                Log.d("add", "toggleCheckedIcon: $pos")
            }
            notifyItemChanged(pos)
        }
    fun removeData(position: Int) {
        videosList.removeAt(position)
        resetCurrentIndex()
        notifyItemRemoved(position)
    }






}

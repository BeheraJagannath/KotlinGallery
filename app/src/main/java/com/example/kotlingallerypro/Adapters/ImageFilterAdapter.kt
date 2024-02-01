package com.example.kotlingallerypro.Adapters

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlingallerypro.Interface.FilterListener
import com.example.kotlingallerypro.R
import net.alhazmy13.imagefilter.ImageFilter

class ImageFilterAdapter (val context: Context, val allFilter: MutableList<ImageFilter.Filter?>, val bitmap: Bitmap,val filterListener: FilterListener)
    : RecyclerView.Adapter<ImageFilterAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, ): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.filter_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.imageView?.setImageBitmap(bitmap)


        } else {
            holder.imageView?.setImageBitmap(
                ImageFilter.applyFilter(
                    bitmap,
                    allFilter[position]
                )
            )
        }
        holder.imageView?.setOnClickListener {
            filterListener.onFilterClick(allFilter.get(position))
            Log.d("Filter", allFilter.get(position).toString())

        }

    }
    override fun getItemCount(): Int {
        return allFilter.size
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageView: ImageView? = itemView.findViewById(R.id.item_filter_image)

    }

}
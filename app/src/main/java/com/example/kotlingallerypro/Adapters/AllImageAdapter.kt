package com.example.kotlingallerypro.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.Utils.Utils
import com.example.kotlingallerypro.modelclass.AllImagesModel

class AllImageAdapter(private var context: Context, private var allImages: List<AllImagesModel>?,
                      var dateList: ArrayList<String> = ArrayList<String>()) :

    RecyclerView.Adapter<AllImageAdapter.ImageItemViewHolder?>() {

    private val allImages1: List<AllImagesModel> = ArrayList<AllImagesModel>()

    var allImagesModels: List<AllImagesModel> = ArrayList<AllImagesModel>()
    private var imageAdapter: ImageAdapter? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemViewHolder {
        var viewHolder: ImageItemViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)
        val v1: View = inflater.inflate(R.layout.image_item, parent, false)
        viewHolder = ImageItemViewHolder(v1)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ImageItemViewHolder, position: Int) {
        val imageItem: AllImagesModel = allImages!![position]
        val imageItemViewHolder = holder
        holder.textViewDate.text = imageItem.date
        if (dateList[position] == "") {
            holder.textViewDate.visibility = View.GONE
        } else {
            holder.textViewDate.visibility = View.VISIBLE
        }
        holder.rv_images.layoutManager = GridLayoutManager(context, Utils.COLUMN_TYPE)
        holder.rv_images.setHasFixedSize(true)
        val allImages1: MutableList<AllImagesModel> = ArrayList<AllImagesModel>()
        val dateList1: MutableList<String> = ArrayList()
        imageAdapter = ImageAdapter(allImages1, dateList1, context)
        holder.rv_images.adapter = imageAdapter
        for (i in allImages!!.indices) {
            if (dateList[position] != "") {
                if (dateList[position] == allImages!![i].getDate()) {
                    allImages1.add(allImages!![i])
                    dateList1.add(dateList[position])
                    holder.rv_images.adapter!!.notifyDataSetChanged()
                }
            }
        }
        holder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return allImages?.size ?: 0
    }


    inner class ImageItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var rv_images: RecyclerView
        var textViewDate: TextView

        init {
            rv_images = itemView.findViewById(R.id.rv_images)
            textViewDate = itemView.findViewById(R.id.txt_date)
        }
    }
    init {
        this.context = context
        this.allImages = allImages
        this.dateList = dateList
    }
}

package com.example.kotlingallerypro.Activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.kotlingallerypro.Adapters.AllImageAdapter
import com.example.kotlingallerypro.Fragments.Gallery_fragment
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.databinding.ActivityAllimageBinding
import com.example.kotlingallerypro.modelclass.AllImagesModel
import java.text.SimpleDateFormat
import java.util.*

class AllimageActivity : AppCompatActivity() {
     lateinit var allimageBinding: ActivityAllimageBinding
     var allImagesList: List<AllImagesModel> = ArrayList<AllImagesModel>()
     var dateList = ArrayList<String>()
     var dateList1 = ArrayList<String>()
     lateinit var allImagesAdapter: AllImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        allimageBinding = DataBindingUtil . setContentView(this,R.layout.activity_allimage)

        allimageBinding.swipe.setOnRefreshListener(OnRefreshListener {
            onStart()
            allimageBinding.swipe.setRefreshing(false)
        })

        allimageBinding.back.setOnClickListener(View.OnClickListener {

            finish()
        })

        showAllImages()
    }

    override fun onStart() {
        super.onStart()
        showAllImages()
    }


    private fun showAllImages() {
        allImagesList = getAllImages() as List<AllImagesModel>
        for (yeas in dateList) {
            if (dateList1.contains(yeas)) {
                dateList1.add("")
            } else {
                dateList1.add(yeas)
            }
        }
        allImagesAdapter = AllImageAdapter(this, allImagesList, dateList1)
        allimageBinding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        allimageBinding.recyclerView.setHasFixedSize(true)
        allimageBinding.recyclerView.adapter = allImagesAdapter
    }

    private fun getAllImages(): List<AllImagesModel?> {
        val allImages: MutableList<AllImagesModel?> = ArrayList<AllImagesModel?>()
        //        List<AllImagesModel> allImages1 = new ArrayList<>();
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_MODIFIED,
            MediaStore.Images.ImageColumns.SIZE,
            MediaStore.Images.ImageColumns.HEIGHT,
            MediaStore.Images.ImageColumns.WIDTH,
            MediaStore.Images.ImageColumns.DATE_TAKEN)
        val cursor = this.contentResolver.query(uri,
            projection,
            null,
            null,
            MediaStore.Images.ImageColumns.DATE_MODIFIED)
        cursor?.moveToFirst()
        try {
            do {
                val allImagesModel = AllImagesModel()
                val id =
                    cursor!!.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID))
                val path =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA))
                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME))
                val date =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED))
                val longDate = date.toLong()
                val dates = Date(longDate * 1000L)
                val dateFormatted = SimpleDateFormat("dd.MM.yyyy").format(dates)
                allImagesModel.id = id.toInt()
                allImagesModel.path = path
                allImagesModel.name = name
                allImagesModel.date = dateFormatted
                allImagesModel.pictureSize = cursor.getString(cursor.getColumnIndexOrThrow(
                    MediaStore.Images.ImageColumns.SIZE))
                allImagesModel.imageHeightWidth = (cursor.getString(cursor.getColumnIndexOrThrow(
                    MediaStore.Images.ImageColumns.HEIGHT)) + " x "
                        + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.WIDTH)))
                try {
                    allImagesModel.dateTaken = cursor.getString(cursor.getColumnIndexOrThrow(
                        MediaStore.Images.ImageColumns.DATE_TAKEN)).toLong()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                allImagesModel.dateModified = cursor.getString(cursor.getColumnIndexOrThrow(
                    MediaStore.Images.ImageColumns.DATE_MODIFIED)).toLong()
                dateList.add (dateFormatted)
                allImages.add(allImagesModel)
            } while (cursor!!.moveToNext())
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Collections.reverse(dateList)
        Collections.reverse(allImages)
        return allImages
    }
}
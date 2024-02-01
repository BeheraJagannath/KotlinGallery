package com.example.kotlingallerypro.Fragments

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlingallerypro.Activity.AllimageSliderActivity
import com.example.kotlingallerypro.Activity.VideoSliderActivity
import com.example.kotlingallerypro.Adapters.FavVideoAdapter
import com.example.kotlingallerypro.Database.DbHelper
import com.example.kotlingallerypro.Interface.AlbumClickInterface
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.Utils.Utils
import com.example.kotlingallerypro.modelclass.ImageSliderModel
import com.example.kotlingallerypro.modelclass.Videomodel
import com.example.kotlingallerypro.modelclass.VideosliderModel
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class FavVideoFragment : Fragment() ,AlbumClickInterface {
    companion object{
        lateinit var recyclerView: RecyclerView
        private var albumVideoList: ArrayList<Videomodel> = ArrayList()
        private var favVideoAdapter: FavVideoAdapter? = null
        lateinit var available : LinearLayout
         var helper: DbHelper? = null
         lateinit var myReceiver: MyReceiver
    }
    class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

//            onResume()

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myReceiver = MyReceiver()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            myReceiver!!,
            IntentFilter("TAG_REFRESH")
        )
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()

    }
    @SuppressLint("NewApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_fav_video, container, false)
        recyclerView = view.findViewById(R.id.fav_videorecycle)
        available = view .findViewById(R.id.no_fav_image_container)

        helper = DbHelper(context, null)

        return  view
    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        setRecyclerView()
    }
    @SuppressLint("NewApi")
    private fun setRecyclerView() {
        albumVideoList.clear()

        helper = DbHelper(activity, null)

        val favModelsList: java.util.ArrayList<String?>? = helper!!.getFavList()


        if (favModelsList != null && favModelsList.size > 0) {
            for (i in favModelsList.indices) {

                var albumVideo = getFavouriteVideo(favModelsList[i]!!)
                if (albumVideo != null) {
                    albumVideoList.add(albumVideo)
                }
            }
        }

        if (albumVideoList != null && albumVideoList.size > 0) {
            available.visibility =View.GONE
            recyclerView.visibility =View.VISIBLE
            recyclerView.layoutManager = GridLayoutManager(context, Utils.COLUMN_TYPE)
            favVideoAdapter = FavVideoAdapter(requireContext() , albumVideoList , this)
            recyclerView.adapter = favVideoAdapter
        } else {
            available.visibility =View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }
    @SuppressLint("UseRequireInsteadOfGet")
    private fun getFavouriteVideo(id: String): Videomodel? {
        var allVideoModel: Videomodel? = null
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.RESOLUTION,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATE_ADDED,
            BaseColumns._ID
        )
        val cursor = requireActivity().contentResolver.query(
            uri,
            projection,
            MediaStore.Images.ImageColumns._ID + " = ?",
            arrayOf(id),
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst()
            do {
                val id = cursor.getString(0)
                val path = cursor.getString(1)
                val title = cursor.getString(2)
                val size = cursor.getInt(3)
                val resolution = cursor.getString(4)
                val duration = cursor.getInt(5)
                val disName = cursor.getString(6)
                val bucket_display_name = cursor.getString(7)
                val width_height = cursor.getString(8)
                val mimeType = cursor.getString(9)
                val dateAdded = cursor.getString(10)
                val columnId  = cursor .getString(11)

                //this method convert 1024 in 1MB
                var human_can_read: String? = null
                human_can_read = if (size < 1024) {
                    String.format(context!!.getString(R.string.size_in_b), size.toDouble())
                } else if (size < Math.pow(1024.0, 2.0)) {
                    String.format(context!!.getString(R.string.size_in_kb), (size / 1024).toDouble())
                } else if (size < Math.pow(1024.0, 3.0)) {
                    String.format(context!!.getString(R.string.size_in_mb),
                        size / Math.pow(1024.0, 2.0))
                } else {
                    String.format(requireContext().getString(R.string.size_in_gb),
                        size / Math.pow(1024.0, 3.0))
                }

                //this method convert any random video duration like 1331533132 into 1:21:12
                var duration_formatted: String
                val sec = duration / 1000 % 60
                val min = duration / (1000 * 60) % 60
                val hrs = duration / (1000 * 60 * 60)
                duration_formatted = if (hrs == 0) {
                    min.toString() + ":" + String.format(Locale.UK, "%02d", sec)
                } else {
                    hrs.toString() + ":" + String.format(Locale.UK,
                        "%02d",
                        min) + ":" + String.format(
                        Locale.UK, "%02d", sec)
                }


                if (path != null && File(path).exists()) {

                    allVideoModel = Videomodel(
                        id, path, title, human_can_read, resolution, duration_formatted,
                        disName, width_height, resolution, bucket_display_name, width_height, mimeType, dateAdded,columnId
                    )
                } else {
                    allVideoModel = null
                }
            } while (cursor.moveToNext())
            cursor.close()
        }
        return allVideoModel
    }

    override fun OnVideoClick(holder: FavVideoAdapter.PictuViewHolder?, video: List<Videomodel?>?, position: Int ) {

        val videosliderModel = VideosliderModel(video, position)
        @SuppressLint("NewApi", "LocalSuppress") val intent = Intent(context, VideoSliderActivity::class.java)
        intent.putExtra("key", videosliderModel)
        startActivity(intent)

    }


}
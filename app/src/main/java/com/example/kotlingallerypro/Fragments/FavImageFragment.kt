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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlingallerypro.Adapters.FavPhotosAdapter
import com.example.kotlingallerypro.Adapters.FavVideoAdapter
import com.example.kotlingallerypro.Database.DbHelper
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.Utils.Utils
import com.example.kotlingallerypro.modelclass.AlbumPictureModel
import com.example.kotlingallerypro.modelclass.Videomodel
import java.io.File


class FavImageFragment : Fragment() {
    companion object{
        lateinit var precyclerView: RecyclerView
        lateinit var favPhotosAdapter: FavPhotosAdapter
        var favimagelist: ArrayList<AlbumPictureModel> = ArrayList()
        lateinit var  dbHelper : DbHelper
        private lateinit var myReceiver: MyReceiver
        lateinit var    null_image : LinearLayout

    }
    private class MyReceiver : BroadcastReceiver() {
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
    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View ? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_fav_image, container, false)
        val view = inflater.inflate ( R.layout.fragment_fav_image,container,false )
        precyclerView = view . findViewById(R.id.fav_photorecycler)
        null_image = view.findViewById(R.id.null_image)

        dbHelper = DbHelper(context ,null )


        return view


    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        setRecyclerView()
    }

    @SuppressLint("NewApi")
    private fun setRecyclerView() {

        favimagelist.clear()

        dbHelper = DbHelper(activity, null)

        val favModelsList: ArrayList<String?>? = dbHelper.getFavList()


        if (favModelsList != null && favModelsList.size > 0) {
            for (i in favModelsList.indices) {

                var albumVideo = getFavImage(favModelsList[i]!!)
                if (albumVideo != null) {
                    favimagelist.add(albumVideo)
                }
            }
        }

        if (favimagelist != null && favimagelist.size > 0) {
            null_image.visibility = View.GONE
            precyclerView.visibility = View.VISIBLE
            precyclerView.layoutManager = GridLayoutManager(context, Utils.COLUMN_TYPE)
            favPhotosAdapter = FavPhotosAdapter(favimagelist,requireContext())
            precyclerView.adapter = favPhotosAdapter
        } else {
            null_image.visibility = View.VISIBLE
            precyclerView.visibility = View.GONE
        }

    }
    private fun getFavImage(imageId: String): AlbumPictureModel? {
        var albumPictureModel: AlbumPictureModel? = null
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.SIZE,
            MediaStore.Images.ImageColumns.HEIGHT,
            MediaStore.Images.ImageColumns.WIDTH,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATE_MODIFIED
        )
        val cursor = requireActivity().contentResolver.query(
            uri,
            projection,
            MediaStore.Images.ImageColumns._ID + " = ?",
            arrayOf(imageId),
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst()
            do {
                val id =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID))
                        .toInt()
                val pictureName =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME))
                val picturePath =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA))
                val pictureSize =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.SIZE))
                val pictureHeightWidth =
                    (cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.HEIGHT)) + " x "
                            + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.WIDTH)))
                var dateTaken: Long = 0
                try {
                    dateTaken =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN))
                            .toLong()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val dateModified =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED))
                        .toLong()
                val columnId =
                    cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)).toString()

                albumPictureModel =
                    if (picturePath != null && File(picturePath).exists()) {
                        AlbumPictureModel(
                            id,  pictureName,  picturePath,  pictureSize,   pictureHeightWidth, columnId,  dateTaken)
                    } else {
                        null
                    }
            } while (cursor.moveToNext())
            cursor.close()
        }
        return albumPictureModel
    }


}






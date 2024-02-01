package com.example.kotlingallerypro.Fragments

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlingallerypro.Adapters.AlbumFolderAdapter
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.Utils.Utils
import com.example.kotlingallerypro.modelclass.AlbumDetail
import com.greedygame.core.adview.general.AdLoadCallback
import com.greedygame.core.adview.general.GGAdview
import com.greedygame.core.models.general.AdErrors
import java.util.*


class Gallery_fragment : Fragment() {
    companion object{
        lateinit var recycler : RecyclerView
//        lateinit var ggAdView : GGAdview
        lateinit var gridLayoutManager: GridLayoutManager
        lateinit var albumFolderAdapter: AlbumFolderAdapter
        var fragmentActivity: FragmentActivity? = null
        private lateinit var myReceiver: MyReceiver
        lateinit var your_parent_layout: LinearLayout
    }
    class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

        }
    }
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myReceiver = MyReceiver()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            myReceiver ,
            IntentFilter("TAG_REFRESH")
        )
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showAlbumFolder()
    }
    private var pictureFolderPath: String? = null
    private var folderName: String? = null

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
       val view: View = inflater.inflate(R.layout.fragment_gallery_fragment, container, false)

       recycler = view.findViewById(R.id.gallery_recycler)
//       ggAdView = view.findViewById(R.id.ggAdView)
       your_parent_layout = view.findViewById(R.id.your_parent_layout)

//       mAdView = view. findViewById(R.id.adView)

//       val adRequest =  AdRequest.Builder().build()
//       ggAdView.loadAd(adRequest)

       val ggAdView = GGAdview(requireContext())
       ggAdView.unitId = "float-13352" //Replace with your Ad Unit ID here

       ggAdView.adsMaxHeight = 150 //Value is in pixels, not in dp


       val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200)
       your_parent_layout.addView(ggAdView, layoutParams)

       ggAdView.loadAd(object:AdLoadCallback{
           override fun onAdLoaded() {
               Log.d("GGADS","Ad Loaded")
           }
           override fun onAdLoadFailed(cause: AdErrors) {
               Log.d("GGADS","Ad Load Failed $cause")
           }
           override fun onUiiOpened() {
               Log.d("GGADS","Uii Opened")
           }
           override fun onUiiClosed() {
               Log.d("GGADS","Uii Closed")
           }
           override fun onReadyForRefresh() {
               Log.d("GGADS","Ad ready for refresh")
           }
       })

       return view
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        showAlbumFolder()
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun showAlbumFolder() {
        recycler . layoutManager =  GridLayoutManager (context, Utils.COLUMN_TYPE)
        recycler . setHasFixedSize(true)
        Log.d("========", "onCreateView: " + getPicturePaths())
        val albumAdapter = getPicturePaths()?.let { context?.let { it1 ->
            AlbumFolderAdapter(it,
                it1)
        } }
        recycler.adapter =  albumAdapter

    }
    @SuppressLint("UseRequireInsteadOfGet")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun getPicturePaths(): ArrayList<AlbumDetail>? {
        val picFolder: ArrayList < AlbumDetail > = ArrayList < AlbumDetail >()
        val picPaths = ArrayList<String>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID
        )
        val cursor = context!!.contentResolver.query(uri, projection, null, null, null)
        try {
            cursor?.moveToFirst()
            do {
                val albumModel = AlbumDetail()
                val name =
                    cursor!!.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                val folder =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                val dataPath =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                var folderPath = dataPath.substring(0, dataPath.lastIndexOf("$folder/"))
                folderPath = "$folderPath$folder/"
                if (!picPaths.contains(folderPath)) {
                    picPaths.add(folderPath)
                    albumModel.setPath(folderPath)
                    albumModel.setFolderName(folder)
                    albumModel.setFirstImage(dataPath)
                    albumModel.addpics()
                    picFolder.add(albumModel)
                } else {
                    for (i in picFolder.indices) {
                        if (picFolder[i].getPath().equals(folderPath)) {
                            picFolder[i].setFirstImage(dataPath)
                            picFolder[i].addpics()
                        }
                    }
                }
            } while (cursor!!.moveToNext())
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
            recycler.setVisibility(View.GONE)
//            textViewNoImage.setVisibility(View.VISIBLE)
        }
        for (i in picFolder.indices) {
            Log.d(
                "GalleryFragment.Java",
                """getPicturePaths: ${picFolder[i].getFolderName().toString() } path : ${ picFolder[i].getPath().toString()} number of pic : ${picFolder[i]
                    .getNumberOfImage().toString() } """
            )
        }
        return picFolder
    }
}




package com.example.kotlingallerypro.Fragments


import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlingallerypro.Adapters.VideoFolderAdapter

import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.Utils.Utils

import com.example.kotlingallerypro.databinding.FragmentVideoFragmentBinding
import com.example.kotlingallerypro.modelclass.VideoFolderModel
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd


class Video_fragment : Fragment() {
    companion object {
        lateinit var videoFolderAdapter: VideoFolderAdapter
        private val videoFolderName: ArrayList<String> = ArrayList()
        lateinit var videoRecycler:RecyclerView
        private lateinit var myReceiver: MyReceiver
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
            myReceiver,
            IntentFilter("TAG_REFRESH")
        )
    }
    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showVideoFolder()
        videoFolderAdapter.notifyDataSetChanged()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View {
        // Inflate the layout for this fragment
//        val binding = DataBindingUtil.inflate<FragmentVideoFragmentBinding>(inflater,
//            R.layout.fragment_video_fragment, container, false)
        val view: View = inflater.inflate(R.layout.fragment_video_fragment, container, false)
        videoRecycler = view.findViewById(R.id.video_recycler)
        showad(view)

        return view
    }

    override fun onResume() {
        super.onResume()
        showVideoFolder()

    }
    private fun showad(view: View) {
        val adLoader = AdLoader.Builder ( requireContext(), getString(R.string.native_ads))
            .forNativeAd { ad : NativeAd ->

                val styles = NativeTemplateStyle.Builder().build()
                val  template : TemplateView = view .findViewById (R.id.template)
                template.setStyles(styles)
                template.setNativeAd(ad)
            }

            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }
    private fun showVideoFolder() {
        videoRecycler.layoutManager = GridLayoutManager(context,Utils.COLUMN_TYPE)
        videoRecycler.isNestedScrollingEnabled = false
        videoFolderAdapter = VideoFolderAdapter(context, videoFolderName, getAllVideoFolder())
        videoRecycler.adapter = videoFolderAdapter
    }
    private fun getAllVideoFolder(): ArrayList<VideoFolderModel>? {
        videoFolderName.clear()

        val tempVideoFolderList = java.util.ArrayList<VideoFolderModel>()

        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Video.VideoColumns._ID,
            MediaStore.Video.VideoColumns.DATA
        )

        val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val videoFolderModel = VideoFolderModel()
                val id =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID))
                val path =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA))

                // storage/sd_card/video_dir/folder_name"/"video_name.mp4
                val slashFirstIndex = path.lastIndexOf("/")
                // "storage/sd_card/video_dir/folder_name"
                val subString = path.substring(0, slashFirstIndex)
                //storage/sd_card/video_dir"/"folder_name
                val index = subString.lastIndexOf("/")
                // folder_name
                val folderName = subString.substring(index + 1, slashFirstIndex)
                if (!videoFolderName.contains(subString)) {
                    videoFolderName.add(subString)
                    videoFolderModel.id = id
                    videoFolderModel.folderName = folderName
                    videoFolderModel.path = subString
                    videoFolderModel.firstPic = path
                    videoFolderModel.addVideo()
                    tempVideoFolderList.add(videoFolderModel)
                } else {
                    for (i in tempVideoFolderList.indices) {
                        if (tempVideoFolderList[i].path.equals(subString)) {
                            tempVideoFolderList[i].firstPic = path
                            tempVideoFolderList[i].addVideo()
                        }
                    }
                }
            }
            cursor.close()
        }
        return tempVideoFolderList
    }
}


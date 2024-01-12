package com.example.kotlingallerypro.Interface

import android.view.View
import com.example.kotlingallerypro.Adapters.AlbumshowAdapter
import com.example.kotlingallerypro.modelclass.AlbumPictureModel

interface SelectedVideoItem {

    fun onPicClicked(holder: AlbumshowAdapter.PictViewHolder?, position: Int, pics: List<AlbumPictureModel?>?)

    fun onItemLongClick(view: View?, obj: AlbumPictureModel?, pos: Int)


}
package com.example.kotlingallerypro.Interface

import android.view.View
import android.widget.RelativeLayout
import com.example.kotlingallerypro.Adapters.FavVideoAdapter
import com.example.kotlingallerypro.Adapters.VideoshowAdapter
import com.example.kotlingallerypro.modelclass.Videomodel
import java.io.File

interface AlbumClickInterface {
    fun OnVideoClick(holder: FavVideoAdapter.PictuViewHolder?,video: List<Videomodel?>? , position: Int )




}
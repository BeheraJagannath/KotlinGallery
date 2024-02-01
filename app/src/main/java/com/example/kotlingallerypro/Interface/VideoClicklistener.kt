package com.example.kotlingallerypro.Interface

import android.view.View
import com.example.kotlingallerypro.Adapters.AlbumshowAdapter
import com.example.kotlingallerypro.Adapters.VideoshowAdapter
import com.example.kotlingallerypro.modelclass.Videomodel
import java.io.File

interface VideoClicklistener {
    fun OnVideoClick(holder: VideoshowAdapter.ViewViewHolders?, video: List<Videomodel?>?, position: Int)
    fun OnVideoLongClick(v: View?, file: File?, position: Int)

}
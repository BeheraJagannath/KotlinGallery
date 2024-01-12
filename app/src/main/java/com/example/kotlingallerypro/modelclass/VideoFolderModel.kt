package com.example.kotlingallerypro.modelclass

class VideoFolderModel {

    var id: String? = null
    var path: String? = null
    var folderName: String? = null
    var firstPic: String? = null
    var numberOfVideo = 0



    fun addVideo() {
        numberOfVideo++
    }
}
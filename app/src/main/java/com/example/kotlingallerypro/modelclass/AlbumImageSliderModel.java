package com.example.kotlingallerypro.modelclass;

import java.io.Serializable;
import java.util.List;

//public class AlbumImageSliderModel {
//}
public class AlbumImageSliderModel implements Serializable {

    List<AlbumPictureModel> albumPictureModelList;
    int position;

    public AlbumImageSliderModel(List<AlbumPictureModel> albumPictureModelList, int position) {
        this.albumPictureModelList = albumPictureModelList;
        this.position = position;
    }

    public List<AlbumPictureModel> getAlbumPictureModelList() {
        return albumPictureModelList;
    }

    public int getPosition() {
        return position;
    }
}

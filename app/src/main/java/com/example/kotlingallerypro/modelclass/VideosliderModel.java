package com.example.kotlingallerypro.modelclass;

import java.io.Serializable;
import java.util.List;




public class VideosliderModel implements Serializable {
    List<Videomodel> allImagesModelList;
    int position;

    public VideosliderModel(List<Videomodel> allImagesModelList, int position) {
        this.allImagesModelList = allImagesModelList;
        this.position = position;
    }

    public List<Videomodel> getAllImagesModelList() {
        return allImagesModelList;
    }

    public int getPosition() {
        return position;
    }

}


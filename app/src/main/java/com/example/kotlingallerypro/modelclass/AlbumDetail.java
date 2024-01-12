package com.example.kotlingallerypro.modelclass;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class AlbumDetail  {


    String path;
    String folderName;
    int numberOfImage = 0;
    String firstImage;

    public AlbumDetail() {
    }

    public AlbumDetail(String path, String folderName, int numberOfImage, String firstImage) {
        this.path = path;
        this.folderName = folderName;
        this.numberOfImage = numberOfImage;
        this.firstImage = firstImage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFolderName() {
        return folderName;
    }

    public void addpics(){
        this.numberOfImage++;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getNumberOfImage() {
        return numberOfImage;
    }

    public void setNumberOfImage(int numberOfImage) {
        this.numberOfImage = numberOfImage;
    }

    public String getFirstImage() {
        return firstImage;
    }

    public void setFirstImage(String firstImage) {
        this.firstImage = firstImage;
    }

}

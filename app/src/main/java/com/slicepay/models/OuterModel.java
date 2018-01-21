package com.slicepay.models;

/**
 * Created by manav on 20/1/18.
 */

public class OuterModel
{
    private String stat;
    private MainModel photos;

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public MainModel getPhotos() {
        return photos;
    }

    public void setPhotos(MainModel photos) {
        this.photos = photos;
    }
}
package com.cksolutions.rosary.prayers.common;

public class PrayerAlbum {
    private String name;
    private int numOfPrayers;
    private int thumbnail;

    public PrayerAlbum() {
    }

    public PrayerAlbum(String name, int numOfPrayers, int thumbnail) {
        this.name = name;
        this.numOfPrayers = numOfPrayers;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getnumOfPrayers() {
        return numOfPrayers;
    }

    public void setnumOfPrayers(int numOfPrayers) {
        this.numOfPrayers = numOfPrayers;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}

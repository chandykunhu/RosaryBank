package com.cksolutions.rosary.prayers.rosary;

public class Rosary {

    private int mysteryNumber;
    private String mysteryName;
    private String mysteryImage;
    private String mysteryStory;


    //Constructor Initialize
    public Rosary(int mysteryNumber,String mysteryName,String mysteryImage,String mysteryStory) {
        this.mysteryNumber = mysteryNumber;
        this.mysteryName = mysteryName;
        this.mysteryImage = mysteryImage;
        this.mysteryStory = mysteryStory;
    }
    public int getmysteryNumber() {
        return mysteryNumber;
    }

    public void setmysteryNumber(int name) {
        this.mysteryNumber = mysteryNumber;
    }

    public String getmysteryName() {
        return mysteryName;
    }

    public void setmysteryName(String mysteryName) {
        this.mysteryName = mysteryName;
    }

    public String getmysteryImage() {
        return mysteryImage;
    }

    public void setmysteryImage(String mysteryName) {
        this.mysteryImage = mysteryImage;
    }

    public String getmysteryStory() {
        return mysteryStory;
    }

    public void setmysteryStory(String mysteryStory) {
        this.mysteryStory = mysteryStory;
    }





}

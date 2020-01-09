package com.adnanhakim.cinematron;

public class SearchItem {

    private int id;
    private String title, year, imageURL;

    public SearchItem(int id, String title, String year, String imageURL) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.imageURL = imageURL;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getImageURL() {
        return imageURL;
    }
}

package com.adnanhakim.cinematron;

public class Movie {

    private int id;
    private String title, overview, imdbRating, imageURL;

    public Movie(int id, String title, String overview, String imdbRating, String imageURL) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.imdbRating = imdbRating;
        this.imageURL = imageURL;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public String getImageURL() {
        return imageURL;
    }
}

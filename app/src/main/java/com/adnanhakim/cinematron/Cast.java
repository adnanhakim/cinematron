package com.adnanhakim.cinematron;

public class Cast {

    private String character, name, imageURL;

    public Cast(String character, String name, String imageURL) {
        this.character = character;
        this.name = name;
        this.imageURL = imageURL;
    }

    public String getCharacter() {
        return character;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }
}

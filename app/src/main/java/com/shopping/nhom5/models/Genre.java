package com.shopping.nhom5.models;

import java.io.Serializable;

public class Genre implements Serializable {
    private String id;
    private String title;
    private String imgUrl;

    public Genre() {

    }

    public Genre(String id) {
        this.id = id;
    }

    public Genre(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public Genre(String id, String title, String imgUrl) {
        this.id = id;
        this.title = title;
        this.imgUrl = imgUrl;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}

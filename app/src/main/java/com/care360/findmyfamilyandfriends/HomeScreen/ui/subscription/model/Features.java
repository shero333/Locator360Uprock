package com.care360.findmyfamilyandfriends.HomeScreen.ui.subscription.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Features implements Serializable {

    private String image;
    private String heading;
    private String text;

    public Features(String image, String heading, String text) {
        this.image = image;
        this.heading = heading;
        this.text = text;
    }

    @NonNull
    @Override
    public String toString() {
        return "Features{" +
                "image='" + image + '\'' +
                ", heading='" + heading + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

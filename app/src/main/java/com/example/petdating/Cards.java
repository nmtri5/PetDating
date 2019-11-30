package com.example.petdating;

import java.io.Serializable;

public class Cards implements Serializable {
    private String userID;
    private String name;
    private String profileImageUrl;
    private String dob, bio, breed;

    public Cards (String userID, String name, String profileImageUrl, String dob, String breed, String bio){
        this.userID = userID;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.dob = dob;
        this.breed = breed;
        this.bio = bio;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.example.petdating.Matches;

public class MatchesObject {
    private String userId;
    private String name;
    private String profileImageUrl;

    public MatchesObject(String userId, String name, String profileImageUrl) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserId() { return userId; }
    public void setMessage(String message) { this.userId = message; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}

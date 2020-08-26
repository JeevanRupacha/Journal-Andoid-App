package com.example.journal.model;


import com.google.firebase.Timestamp;

public class JournalDetails {

    private String title;
    private String thought;
    private String userId;
    private String imageUrl;
    private String userName;
    private Timestamp timeAdded;

    public JournalDetails(String title, String thought, String userId,
                          String imageUrl, String userName, Timestamp timeAdded) {
        this.title = title;
        this.thought = thought;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.userName = userName;
        this.timeAdded = timeAdded;
    }

    public JournalDetails(){}

    public String getTitle() {
        return title;
    }

    public String getThought() {
        return thought;
    }

    public String getUserId() {
        return userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }
}

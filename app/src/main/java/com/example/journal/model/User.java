package com.example.journal.model;

import android.app.Application;

public class User extends Application {

    private String email,password,username;
    private String dateOfBirth, birthPlace;
    private String profileImageId;

    private String currentUserId;

    private static User instance;

    public static User getInstance(){
        if (instance == null)
            instance = new User();
        return instance;
    }

    public User(){}

    public User(String email, String password, String username){
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User(String email, String password, String username,
                String dateOfBirth, String birthPlace, String profileImageId) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.dateOfBirth = dateOfBirth;
        this.birthPlace = birthPlace;
        this.profileImageId = profileImageId;
    }

    public String getCurrentUserId(){
        return currentUserId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public String getProfileImageId() {
        return profileImageId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public void setProfileImageId(String profileImageId) {
        this.profileImageId = profileImageId;
    }
    public void setCurrentUserId(String currentUserId){
        this.currentUserId = currentUserId;
    }
}

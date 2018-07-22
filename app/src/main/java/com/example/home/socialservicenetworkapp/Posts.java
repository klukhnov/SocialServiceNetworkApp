package com.example.home.socialservicenetworkapp;

public class Posts {
    public String uid, time, date, postimage, descripiton, profileimage, fullname;

    public Posts(){

    }

    public Posts(String uid, String time, String date, String postimage, String descripiton, String profileimage, String fullname) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postimage = postimage;
        this.descripiton = descripiton;
        this.profileimage = profileimage;
        this.fullname = fullname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescripiton() {
        return descripiton;
    }

    public void setDescripiton(String descripiton) {
        this.descripiton = descripiton;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}

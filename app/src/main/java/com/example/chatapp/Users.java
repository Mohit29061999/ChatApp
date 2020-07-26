package com.example.chatapp;



public class Users {
    public String name ;
    public String image;
    public String status;
    public String thumb_image;
    public long lastSeen;
    public String online;
    public String device_token;


    public Users(String name, String image, String status, String thumb_image, long lastSeen, String online, String device_token) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.thumb_image = thumb_image;
        this.lastSeen = lastSeen;
        this.online = online;
        this.device_token = device_token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public Users() {
    }




}


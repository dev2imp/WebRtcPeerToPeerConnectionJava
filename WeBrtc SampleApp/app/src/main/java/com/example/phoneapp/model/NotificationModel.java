package com.example.phoneapp.model;


import android.content.Context;

public class NotificationModel {
    public  String token;
    public Context context;
    public String message;

    public NotificationModel(String token, Context context, String message) {
        this.token = token;
        this.context = context;
        this.message = message;
    }
}

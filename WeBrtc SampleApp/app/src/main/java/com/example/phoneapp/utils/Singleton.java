package com.example.phoneapp.utils;

import android.content.Context;

import com.example.phoneapp.model.FirebaseServiceRepository;
import com.example.phoneapp.model.RtcRepository;
import com.google.firebase.firestore.FirebaseFirestore;

public class Singleton {
    /*
    we want to have only one FirebaseServiceRepository object.
    when new token is generated we will let repository know it.
    as it know it wil postValue to The observable object.
    viewModel that listen this value will be triggered.
    */
    /*
    FCM_API:
    this is default link to Push notificaiton
     */
    public static Context context;
    public static String Token;
    public static RtcRepository rtcRepository;
    public static  String FCM_API = "https://fcm.googleapis.com/fcm/send";
    public static  String contentType = "application/json";
    //Server key format--> key:Server_key
    public static String ServerKey="key=AAAASvJAzn8:APA91bFdejU78Pih0q12rVp38OEGDxGUkLo332iitycvlNqFVN25OTy6zdmwR1zlSPgJeyNd_XJdtIg7y0HbH1aQPyCD3OrS0JupJ1GWDNsk_gsnqKZbSSxD1kFFP27y2WX1bt77R95B";
    static Singleton singleton =null;
   public static FirebaseServiceRepository repository;
   public static FirebaseFirestore  firestoredb;
   public static Singleton   getInstance(Context context){
       //generate only one signleton.
        if(singleton==null){
            singleton= new  Singleton();
            rtcRepository = new RtcRepository();
            repository = new FirebaseServiceRepository();
            firestoredb=  FirebaseFirestore.getInstance();
        }
        return singleton;
    }
}

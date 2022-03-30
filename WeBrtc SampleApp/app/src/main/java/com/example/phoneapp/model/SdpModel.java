package com.example.phoneapp.model;

import org.webrtc.SessionDescription;

public class SdpModel {
  public   String description;
    public  String type;

    public SdpModel(String description, String type) {
        this.description = description;
        this.type = type;
    }
    
}

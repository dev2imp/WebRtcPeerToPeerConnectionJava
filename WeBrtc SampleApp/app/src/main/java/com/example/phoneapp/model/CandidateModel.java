package com.example.phoneapp.model;

public class CandidateModel {
    public String   serverUrl;
    public String   sdpMid;
    public String   sdpMLineIndex;
    public String   sdpCandidate;
    public String   type;
    public CandidateModel(String serverUrl, String sdpMid, String sdpMLineIndex, String sdpCandidate, String type) {
        this.serverUrl = serverUrl;
        this.sdpMid = sdpMid;
        this.sdpMLineIndex = sdpMLineIndex;
        this.sdpCandidate = sdpCandidate;
        this.type = type;
    }
}

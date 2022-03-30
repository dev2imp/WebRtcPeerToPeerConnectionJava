package com.example.phoneapp.view.videocall;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

public interface SignalListener {
    void onConnectionEstablished();
    void onOfferReceived(SessionDescription description);
    void onAnswerReceived(SessionDescription description);
    void onIceCandidateReceived(IceCandidate iceCandidate);
    void onCallEnded();
}



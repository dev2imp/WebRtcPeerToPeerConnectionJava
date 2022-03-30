package com.example.phoneapp.view.videocall;

import android.app.Application;
import android.util.Log;

import com.example.phoneapp.utils.Singleton;

import org.webrtc.SessionDescription;

public class SdpObserver implements org.webrtc.SdpObserver {

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        Log.d("SdpObserver->","onCreateSuccess"+sessionDescription);

    }
    @Override
    public void onSetSuccess() {
        Log.d("SdpObserver->","onSetSuccess");
    }

    @Override
    public void onCreateFailure(String s) {
        Log.d("SdpObserver->","onCreateFailure"+s);
    }

    @Override
    public void onSetFailure(String s) {
        Log.d("SdpObserver->","onSetFailure"+s);
    }
}

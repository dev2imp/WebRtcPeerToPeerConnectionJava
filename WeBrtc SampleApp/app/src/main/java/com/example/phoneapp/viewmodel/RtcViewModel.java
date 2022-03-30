package com.example.phoneapp.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.phoneapp.model.CandidateModel;
import com.example.phoneapp.model.SdpModel;
import com.example.phoneapp.utils.Singleton;

import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;

public class RtcViewModel extends ViewModel {
    public void Initlize(Context context) {
        Singleton.rtcRepository.Initlize(context);
    }

    public void CreateOffer() {
        Singleton.rtcRepository.CreateOffer();
    }

    public void setRemoteAnswer() {
        Singleton.rtcRepository.setRemoteAnswer();
    }

    public void setIceCandidate() {
        Singleton.rtcRepository.setIceCandidate();
    }

    public void sendData() {
        Singleton.rtcRepository.sendData();
    }

    public void close() {
        Singleton.rtcRepository.close();
    }

    public LiveData<SdpModel> SDP = Singleton.rtcRepository.SDP;
    public LiveData<CandidateModel> CandidateModel = Singleton.rtcRepository.CandidateModel;

    public LiveData<String> ConnState = Singleton.rtcRepository.ConnState;
    public LiveData<Boolean> Renegotiate = Singleton.rtcRepository.Renegotiate;



}

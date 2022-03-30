package com.example.phoneapp.view.videocall;

import android.util.Log;

import com.example.phoneapp.utils.Singleton;

import org.webrtc.CandidatePairChangeEvent;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpTransceiver;

public class PeerConnectionObserver implements PeerConnection.Observer {
    String TAG="PeerConnectionObserver";
    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        Log.d(TAG,"->onSignalingChange->"+signalingState);

    }
    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        Log.d(TAG,"->onIceConnectionChange->"+iceConnectionState);
    }

    @Override
    public void onStandardizedIceConnectionChange(PeerConnection.IceConnectionState newState) {
        Log.d(TAG,"->onStandardizedIceConnectionChange->"+newState);
    }

    @Override
    public void onConnectionChange(PeerConnection.PeerConnectionState newState) {
        Log.d(TAG,"->onConnectionChange->"+newState);
        Singleton.rtcRepository.ConnectionChange(newState.toString());
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
        Log.d(TAG,"->onIceConnectionReceivingChange->");
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        Log.d(TAG,"->onIceGatheringChange->");
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        Log.d(TAG,"->onIceCandidate->");



    }
    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        Log.d(TAG,"->onIceCandidatesRemoved->");
    }

    @Override
    public void onSelectedCandidatePairChanged(CandidatePairChangeEvent event) {
        Log.d(TAG,"->onSelectedCandidatePairChanged->"+event);
    }
    @Override
    public void onAddStream(MediaStream mediaStream) {
        Log.d(TAG,"->onAddStream->"+mediaStream);
    }
    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        Log.d(TAG,"->onRemoveStream->"+mediaStream);
    }
    @Override
    public void onDataChannel(DataChannel dataChannel) {
        Log.d(TAG,"->onDataChannel->"+dataChannel);
    }
    @Override
    public void onRenegotiationNeeded() {
        Log.d(TAG,"->onRenegotiationNeeded->");
        Singleton.rtcRepository.RenegotiateNeeded();
    }
    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
        Log.d(TAG,"->onAddTrack->"+rtpReceiver);
    }
    @Override
    public void onTrack(RtpTransceiver transceiver) {
        Log.d(TAG,"->onTrack->"+transceiver);
    }
}

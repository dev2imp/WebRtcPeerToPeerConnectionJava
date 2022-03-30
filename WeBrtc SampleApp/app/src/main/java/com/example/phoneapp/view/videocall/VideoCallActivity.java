package com.example.phoneapp.view.videocall;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import com.example.phoneapp.R;
import com.example.phoneapp.model.CandidateModel;
import com.example.phoneapp.model.SdpModel;
import com.example.phoneapp.utils.Singleton;
import com.example.phoneapp.viewmodel.RtcViewModel;

import org.webrtc.AudioTrack;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.voiceengine.WebRtcAudioManager;

public class VideoCallActivity extends AppCompatActivity implements SignalListener {
    Boolean isJoin=false;
    RTCClient rtcClient;
    RtcViewModel rtcViewModel;
    Button callButon;
    SurfaceViewRenderer Remote_video;
    SurfaceViewRenderer Local_video;
    EglBase rootEglBase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        callButon = findViewById(R.id.callButton);
        Remote_video = findViewById(R.id.Remote_video);
        Local_video = findViewById(R.id.Local_video);;
       //todo Audio

        //viewmodel to observe or triger action for repository
        rtcViewModel = new RtcViewModel();
        rtcViewModel.SDP.observe(this, new Observer<SdpModel>() {
            @Override
            public void onChanged(SdpModel sdpModel) {
                if (sdpModel.type.equals("OFFER")) {
                    //other peer user sent offer we receive and generate answer
                    SessionDescription description = new SessionDescription(SessionDescription.Type.OFFER, sdpModel.description);
                    onOfferReceived(description);
                } else if (sdpModel.type.equals("ANSWER")) {
                    //our Answer received by other peer user
                    SessionDescription description = new SessionDescription(SessionDescription.Type.ANSWER, sdpModel.description);
                    onAnswerReceived(description);
                }
            }
        });
        rtcViewModel.CandidateModel.observe(this, new Observer<CandidateModel>() {
            @Override
            public void onChanged(CandidateModel candidateModel) {
                Log.d("VA CandidateModel->",candidateModel.toString());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        onIceCandidateReceived(
                          new IceCandidate(candidateModel.sdpMid,
                                        Math.toIntExact(Long.parseLong(candidateModel.sdpMLineIndex)),
                                        candidateModel.sdpCandidate));
                    }
            }
        });
        rtcViewModel.ConnState.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                DisplayConnState(s);
            }
        });
        rtcViewModel.Renegotiate.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                RenegotiateNeeded();
            }
        });
        rootEglBase = EglBase.create();
        callButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isJoin = true;
                SetUpRTCLClient();
                MakeCall();
            }
        });
    }
    private void DisplayConnState(String s) {
        TextView connstate = findViewById(R.id.connState);
        connstate.setText(s);
    }
    private void SetUpRTCLClient() {
        rtcClient = new RTCClient(this, new PeerConnectionObserver(){
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                CandidateChangeObservered(iceCandidate);
            }
            @Override
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
                Log.d("videoActivity", " onAddStream-->"+mediaStream.videoTracks);
                mediaStream.videoTracks.get(0).addSink(Remote_video);
                mediaStream.videoTracks.get(0).setEnabled(true);
                Log.d("videoActivity", " onAddStream-audioTracks->"+mediaStream.audioTracks);

                WebRtcAudioManager.setStereoOutput(true);
            }
        }, rootEglBase);
        rtcClient.initSurfaceView(Local_video);
        rtcClient.StartLocalVideo(Local_video);
        rtcClient.initSurfaceView(Remote_video);
    }
    private void MakeCall() {
        Log.d("VideocallActivity->", "MakeCall->");
        isJoin = true;
        rtcClient.call(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription){
                super.onCreateSuccess(sessionDescription);
                //as we have created off we want to notify other end user
                //then we set this description to ours as local description while thy will
                //will set as remote.
                Log.d("VideocallActivity->", "MakeCall->onCreateSuccess");
                SetLocalDecsription(sessionDescription);
            }
        });
    }
    //SignalListeners methods
    @Override
    public void onConnectionEstablished() {
        Log.d("VideocallActivity->", "onConnectionEstablished");
    }
    @Override
    public void onOfferReceived(SessionDescription description) {
        Log.d("VideocallActivity->", "onOfferReceived:" + description);
        //as we have received offer from other user we want to setup our peerconection
        SetUpRTCLClient();
        //as we get description from other user we set it as remote description.
        rtcClient.onRemoteSessionReceived(new SdpObserver(){
            @Override
            public void onSetSuccess() {
                super.onSetSuccess();
                //as we set successfuly we want generate answer
                rtcClient.Answer(new SdpObserver() {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                        super.onCreateSuccess(sessionDescription);
                        Log.d("VideocallActivity->", "onOfferReceived->Answer-onCreateSuccess" + sessionDescription.description);
                        // we have genereated answer as we notify other user
                        //we set this as local description.
                        SetLocalDecsription(sessionDescription);
                    }
                    @Override
                    public void onCreateFailure(String s) {
                        super.onCreateFailure(s);
                        Log.d("VideocallActivity->", "onOfferReceived->Answer-onCreateFailure" + s);
                    }
                });
            }
        },description);
    }
    @Override
    public void onAnswerReceived(SessionDescription description) {
        Log.d("VideocallActivity->", "onAnswerReceived" + description);
        if(rtcClient==null){
            SetUpRTCLClient();
            MakeCall();
        }else{
            //we got answer from other peer user so we set it remot
            rtcClient.onRemoteSessionReceived(new SdpObserver(){
                @Override
                public void onCreateSuccess(SessionDescription sessionDescription) {
                    super.onCreateSuccess(sessionDescription);
                    Log.d("VideocallActivity->", "onAnswerReceived->onCreateSuccess->" + sessionDescription);
                }
                @Override
                public void onCreateFailure(String s) {
                    super.onCreateFailure(s);
                    Log.d("VideocallActivity->", "onAnswerReceived->onCreateFailure->" + s);
                }
            },description);
        }
    }
    @Override
    public void onIceCandidateReceived(IceCandidate iceCandidate) {
        //we have candidate from other end user.
        Log.d("IceCandidate->", "onIceCandidateReceived");
        rtcClient.addIceCandidate(iceCandidate);
    }
    @Override
    public void onCallEnded() {

    }
    //Methods to Run RTCRepository o take action
    private void PushAnswerAndNotify(SessionDescription sessionDescription) {
        Singleton.rtcRepository.PushAnswerAndNotify(this, sessionDescription);
    }
    private void PushOfferNotify(SessionDescription sessionDescription) {
        Singleton.rtcRepository.PushOfferAndNotify(this, sessionDescription);
    }
    private void PushOfferCandidateAndNotify(IceCandidate iceCandidate) {
        Singleton.rtcRepository.PushOfferCandidateAndNotify(this, iceCandidate);
    }
    private void PushAnswerCandidateAndNotify(IceCandidate iceCandidate){
        Singleton.rtcRepository.PushAnswerCandidateAndNotify(this, iceCandidate);
    }
    //Call Methods in RTCClient
    //Set Local desc as Local desc not setup No candidate generated.
    private void SetLocalDecsription(SessionDescription sessionDescription) {
        //set sessionDescription locally
        rtcClient.setLocalDesc(new SdpObserver() {
            @Override
            public void onSetSuccess() {
                super.onSetSuccess();
                //sessionDescription locally as set successed push to other user.
                Log.d("videoActivity", " rtcClient.setLocalDesc onSetSuccess");
                //send answer to the user thats sent offer
                if(isJoin){
                    Log.d("videoActivity", " MakeCall() onCreateSuccess");
                    //push sdp to other end user
                    PushOfferNotify(sessionDescription);
                }else{
                    PushAnswerAndNotify(sessionDescription);
                }
            }
        }, sessionDescription);
    }
    //Observed via Viewmodel from PeerConnectionObserver methods
    //it is triggered from RTCRepository
    private void CandidateChangeObservered(IceCandidate iceCandidate) {
        //becase we will try to connect local network
        //simple generate offer as candidate change receivded.

        //we need commented part below if we need to peer connection out of local that time we have to work with turn stun server.

        if (rtcClient != null) {
            Log.d("İsJon-->", isJoin.toString());
            if (isJoin) {
                //each time new candidate is received we want to
                //resedn the Sdp
                MakeCall();
               PushOfferCandidateAndNotify(iceCandidate);
            } else {
                PushAnswerCandidateAndNotify(iceCandidate);
            }
            rtcClient.addIceCandidate(iceCandidate);
        }

    }
    private void RenegotiateNeeded(){
        if(isJoin){
            //MakeCall();
        }
    }
}

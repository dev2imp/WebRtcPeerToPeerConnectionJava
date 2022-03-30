package com.example.phoneapp.view.videocall;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;


import com.example.phoneapp.utils.Singleton;
import com.google.rpc.context.AttributeContext;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera2Enumerator;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.voiceengine.WebRtcAudioManager;

import java.util.ArrayList;
import java.util.List;
public class RTCClient {
    Context context;
    PeerConnection.Observer observer;
    List<PeerConnection.IceServer> iceServers = new ArrayList<>();
    PeerConnection peerConnection;
    PeerConnectionFactory peerConnectionFactory;
    EglBase rootEglBase;
    VideoSource localVideoSource;
    AudioSource localAudioSource;
    VideoTrack localVideoTrack;
    AudioTrack localAudioTrack;
    MediaStream localStream;

    public RTCClient(Context context, PeerConnection.Observer observer,EglBase rootEglBase) {
        this.context = context;
        this.observer = observer;
        this.rootEglBase=rootEglBase;
        initPeerConnectionFactory(context);
      iceServers.add(PeerConnection.IceServer.builder("stun.l.google.com:19302").createIceServer());

        Log.d("RTCClient","iceServers->"+iceServers);
    }

    private void initPeerConnectionFactory(Context context) {

        VideoEncoderFactory encoderFactory = new DefaultVideoEncoderFactory(rootEglBase.getEglBaseContext(), true, true);
        VideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());
      PeerConnectionFactory.InitializationOptions initializationOptions = PeerConnectionFactory.InitializationOptions.builder(context)
              .setEnableInternalTracer(true)
              .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
              .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        options.disableEncryption=true;
        options.disableNetworkMonitor=true;
         peerConnectionFactory = PeerConnectionFactory.builder()
                .setVideoDecoderFactory(decoderFactory)
                .setVideoEncoderFactory(encoderFactory)
                 .setOptions(options)
                .createPeerConnectionFactory();

        PeerConnection.RTCConfiguration rtcConfig =   new PeerConnection.RTCConfiguration(iceServers);
        peerConnection =peerConnectionFactory.createPeerConnection(rtcConfig,observer);
        //SetUp Audio video source and track
        SetupAudioVideo();
    }

    public void onRemoteSessionReceived(SdpObserver sdpObserver, SessionDescription description) {
        peerConnection.setRemoteDescription(sdpObserver,description);
    }
    public void setLocalDesc(SdpObserver sdpObserver, SessionDescription sessionDescription) {
        peerConnection.setLocalDescription(sdpObserver,sessionDescription);
    }
    public void addIceCandidate(IceCandidate iceCandidate) {
        //notigy user about conneciton
       peerConnection.addIceCandidate(iceCandidate);
        Log.d("RTCClient","addIceCandidate=>"+iceCandidate.toString());
    }
    public void call(SdpObserver sdpObserver) {
        MediaConstraints constraints=new MediaConstraints();
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        peerConnection.createOffer(sdpObserver,constraints);
    }
    public void Answer(SdpObserver sdpObserver) {
        MediaConstraints constraints=new MediaConstraints();
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        peerConnection.createAnswer(sdpObserver,constraints);
    }

    public void initSurfaceView(SurfaceViewRenderer viewRenderer){
        try{
            viewRenderer.init(rootEglBase.getEglBaseContext(),null);
        }catch (Exception e){
        }
        viewRenderer.setMirror(true);
        viewRenderer.setEnableHardwareScaler(true);
    }
    VideoCapturer getVideoCapturer(Context  context){
        Camera2Enumerator enumerator =  new Camera2Enumerator(context);
        //getting camera names
        String[] camera = enumerator.getDeviceNames();
        VideoCapturer  videoCapturer= enumerator.createCapturer(camera[0],null);
        Log.d("RTCCLient-->","initSurfaceView-Done+"+videoCapturer);
        return videoCapturer;
    }
    private void SetupAudioVideo(){
        //Audio
        MediaConstraints constraints=new MediaConstraints();
        localAudioSource =peerConnectionFactory.createAudioSource(constraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack("AUDIO_TRACK_ID", localAudioSource);
        localAudioTrack.setEnabled(true);
        //Video
        localVideoSource = peerConnectionFactory.createVideoSource(false);
        localVideoTrack =peerConnectionFactory.createVideoTrack("VIDEO_TRACK_ID",localVideoSource);
    }
    public void StartLocalVideo(SurfaceViewRenderer local_video) {
        SurfaceTextureHelper helper =SurfaceTextureHelper.create(Thread.currentThread().getName(),rootEglBase.getEglBaseContext());
        VideoCapturer videoCapturer= getVideoCapturer(context);
        videoCapturer.initialize( helper,
                local_video.getContext(),
                localVideoSource.getCapturerObserver());
        videoCapturer.startCapture(320, 240, 60);
        localVideoTrack.addSink(local_video);
        localStream = peerConnectionFactory.createLocalMediaStream("LOCAL_MEDIA_STREAM");
        localStream.addTrack(localVideoTrack);
        localStream.addTrack(localAudioTrack);
        peerConnection.addStream(localStream);
    }
    /*
    TODO
        if userA will call userB
        UserA will click camera on UserB and later click button call.
        meantime UserB has to be viewing UserA where we initilize RTCp connecion connection.
        Like ready waiting for call.
     */
}

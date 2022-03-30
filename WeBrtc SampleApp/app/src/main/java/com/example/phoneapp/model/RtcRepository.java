package com.example.phoneapp.model;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.phoneapp.utils.Singleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.SessionDescription;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RtcRepository {
    public void Initlize(Context context) {

    }

    public void CreateOffer() {

    }
    public void setRemoteAnswer() {

    }
    public void setIceCandidate() {

    }
    public void sendData() {

    }
    public void close() {

    }
    public void PushOfferAndNotify(Context context,SessionDescription sessionDescription){
        Log.d("sdfasf","asdfasfsadf");
        HashMap<String,String> post = new HashMap<>();
        post.put("sdp",sessionDescription.description);
        post.put("type","OFFER");
        Singleton.firestoredb.document("calls/offers").set(post);
        //send notificaition to suer
        Log.d("Token->",Singleton.Token);
        Singleton.repository.SendNotificaitonToUser(new NotificationModel(Singleton.Token,context,"getOffer"));
    }
    public void PushAnswerAndNotify(Context context,SessionDescription sessionDescription){
        Log.d("sdfasf","asdfasfsadf");
        HashMap<String,String> post = new HashMap<>();
        post.put("sdp",sessionDescription.description);
        post.put("type","ANSWER");
        Singleton.firestoredb.document("calls/answer").set(post);
        //send notificaition to suer
        Log.d("Token->",Singleton.Token);
        Singleton.repository.SendNotificaitonToUser(new NotificationModel(Singleton.Token,context,"getAnswer"));
    }
    public void PushCallStateAndNotify(Context context,SessionDescription sessionDescription){
        Log.d("sdfasf","asdfasfsadf");
        HashMap<String,String> post = new HashMap<>();
        post.put("sdp",sessionDescription.description);
        post.put("type","END_CALL");
        Singleton.firestoredb.document("calls/callstate").set(post);
        //send notificaition to suer
        Log.d("Token->",Singleton.Token);
        Singleton.repository.SendNotificaitonToUser(new NotificationModel(Singleton.Token,context,"getCallState"));
    }
    public void PushOfferCandidateAndNotify(Context context, IceCandidate iceCandidate){

        HashMap<String,String> post = new HashMap<>();
        String IceString=iceCandidate.sdp;
       // IceString= IceString.replaceAll("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+",myIP);
        post.put( "serverUrl" , iceCandidate.serverUrl);
        post.put( "sdpMid" , iceCandidate.sdpMid);
        post.put( "sdpMLineIndex" , String.valueOf(iceCandidate.sdpMLineIndex));
        post.put( "sdpCandidate" ,IceString);
        post.put( "type" , "offercandidate");
        Singleton.firestoredb.document("calls/offercandidate").set(post);
        //send notificaition to suer
        Log.d("Token->",Singleton.Token);
        Singleton.repository.SendNotificaitonToUser(new NotificationModel(Singleton.Token,context,"getOfferCandidate"));
    }
    public void PushAnswerCandidateAndNotify(Context context, IceCandidate iceCandidate){
        HashMap<String,String> post = new HashMap<>();
        String IceString=iceCandidate.sdp;
       // IceString= IceString.replaceAll("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+",myIP);

        post.put( "serverUrl" , iceCandidate.serverUrl);
        post.put( "sdpMid" , iceCandidate.sdpMid);
        post.put( "sdpMLineIndex" , String.valueOf(iceCandidate.sdpMLineIndex));
        post.put( "sdpCandidate" , IceString);
        post.put( "type" , "answercandidate");

        Singleton.firestoredb.document("calls/answercandidate").set(post);
        //send notificaition to suer
        Log.d("Token->",Singleton.Token);
        Singleton.repository.SendNotificaitonToUser(new NotificationModel(Singleton.Token,context,"getAnswerCandidate"));
    }
    public  MutableLiveData<SdpModel> SDP =new MutableLiveData();
    public void getOfferfromDb(){
        Log.d("RTCRepository->","getOfferfromDb");
        Singleton.firestoredb.document("calls/offers").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String sdp= task.getResult().get("sdp").toString();
                String type= task.getResult().get("type").toString();
                if(type.equals("OFFER")){
                    Log.d("-OFFER-sdp->",  sdp);
                    SDP.postValue(new SdpModel( sdp,type));
                }
            }
        });
    }
    public void getAnswersfromDb(){
        Log.d("RTCRepository->","getAnswersfromDb");
        Singleton.firestoredb.document("calls/answer").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String sdp= task.getResult().get("sdp").toString();
                String type= task.getResult().get("type").toString();
                 if(type.equals("ANSWER")){
                    Log.d("ANSWER-sdp->", sdp);
                    SDP.postValue(new SdpModel( sdp,type));
                }
            }
        });
    }
    public void getCallStatefromDb(){
        Log.d("getSDPfromDb->","getCallStatefromDb");
        Singleton.firestoredb.document("calls/callstate").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                String sdp= task.getResult().get("sdp").toString();
                String type= task.getResult().get("type").toString();
                if(type.equals("END_CALL")){
                    Log.d("ANSWER-sdp->", sdp);
                    SDP.postValue(new SdpModel( sdp,type));
               }
            }
        });
    }
    public  MutableLiveData<CandidateModel> CandidateModel =new MutableLiveData();
    public void getOfferCandidatefromDb(){
        Log.d("getSDPfromDb->","getOfferCandidatefromDb");
        Singleton.firestoredb.document("calls/offercandidate").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task){

                String type= task.getResult().get("type").toString();
                if(type.equals("offercandidate")){
                    String serverUrl= task.getResult().get("serverUrl").toString();
                    String sdpMid= task.getResult().get("sdpMid").toString();
                    String sdpMLineIndex= task.getResult().get("sdpMLineIndex").toString();
                    String sdpCandidate= task.getResult().get("sdpCandidate").toString();
                    CandidateModel.postValue(new CandidateModel(
                            serverUrl, sdpMid,
                        sdpMLineIndex,sdpCandidate,
                        type
                    ));
                }
            }
        });
    }
    public void getAnswerCandidatefromDb(){
        Log.d("getSDPfromDb->","getAnswerCandidatefromDb");
        Singleton.firestoredb.document("calls/answercandidate").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                String type= task.getResult().get("type").toString();
                if(type.equals("answercandidate")){
                    String serverUrl= task.getResult().get("serverUrl").toString();
                    String sdpMid= task.getResult().get("sdpMid").toString();
                    String sdpMLineIndex= task.getResult().get("sdpMLineIndex").toString();
                    String sdpCandidate= task.getResult().get("sdpCandidate").toString();

                    CandidateModel.postValue(new CandidateModel(
                            serverUrl, sdpMid,
                            sdpMLineIndex,sdpCandidate,
                            type
                    ));
                }
            }
        });
    }
    public MutableLiveData<String> ConnState=new MutableLiveData();
    public void ConnectionChange(String state) {
        ConnState.postValue(state);
    }

    public MutableLiveData<Boolean> Renegotiate=new MutableLiveData();
    public void RenegotiateNeeded() {
        Renegotiate.postValue(true);
    }
}

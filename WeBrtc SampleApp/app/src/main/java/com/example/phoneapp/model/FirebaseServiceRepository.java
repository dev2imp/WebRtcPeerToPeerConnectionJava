package com.example.phoneapp.model;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.phoneapp.utils.Singleton;
import com.example.phoneapp.view.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FirebaseServiceRepository {
    public  MutableLiveData<String> Token = new MutableLiveData<>();
    /*
    as token change the new value will be set to Token
        which is LiveData that will be observed
        then we can save new token to server.
        we know that token has changed and we have to save it
        in out backend server.
        as The   observes Token will be notified.
        we can directly update token From here.
     */
 public  void TokenHasChanged(String token){
        Token.postValue(token);
        //update token at backend server or add
        Log.d("token to server->",token);
    }
    public  void UpdateTokenAtServer(String username){
        String token = Token.getValue();
        HashMap<String,String> post = new HashMap<>();
        post.put("token",token);
        post.put("username",username);
        /*
        we will use name of user as document name
        to let users to message and call each other.
         */
        String name = username.replace(" ","").toLowerCase(Locale.ROOT);
        Singleton.firestoredb.document("users/"+name).set(post);
    }
    /*
    get all users and post data to UsersArray
    as user fetched Observer will know it.
     */
    public  MutableLiveData<ArrayList<UserModel>> UsersArray = new MutableLiveData<>();
    private ArrayList<UserModel> TempArr = new ArrayList();
    public void FetchUsers(){
     /*
     Get all users from Firestore
     */
        Singleton.firestoredb.collection("users").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc: task.getResult().getDocuments()){
                    Log.d("username->",doc.get("username").toString());
                    Log.d("token->",doc.get("token").toString());
                    String username= doc.get("username").toString();
                    String token = doc.get("token").toString();
                    TempArr.add(new UserModel(username,token));
                }
                UsersArray.postValue(TempArr);
            }
        });
    }
    public void SendNotificaitonToUser(NotificationModel notificationModel){
        SendMessageToUser(notificationModel);
    }
    private void SendMessageToUser(NotificationModel notificationModel)
    {
        Log.d("A->","SendMessageToUser");
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", notificationModel.message);
            notifcationBody.put("body", notificationModel.message);
            notification.put("to",notificationModel.token); //Receiver_Token
            notification.put("notification", notifcationBody);
            sendNotification(notificationModel,notification);
        } catch (JSONException e) {
            Log.d("ee->",e.toString());
        }
    }
    private void sendNotification(NotificationModel notificationModel,JSONObject notification) {
        Log.d("ServerKey->",Singleton.ServerKey);
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST,Singleton.FCM_API, notification,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("-sendNotification-OnResponse->",response.toString());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("sendNotification-OnResponse->",error.toString());
                            }
                        }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        //I need to get server key from server NEED TO CHANGE THİS
                        params.put("Authorization", Singleton.ServerKey);
                        params.put("Content-Type", Singleton.contentType);
                        return params;
                    }
                };
        //putting que to nnotify user
        MainActivity.SingleUploader.getInstance(notificationModel.context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
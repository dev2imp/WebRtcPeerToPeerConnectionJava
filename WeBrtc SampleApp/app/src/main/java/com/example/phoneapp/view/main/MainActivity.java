package com.example.phoneapp.view.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.phoneapp.R;
import com.example.phoneapp.model.NotificationModel;
import com.example.phoneapp.model.UserModel;
import com.example.phoneapp.utils.Singleton;
import com.example.phoneapp.view.videocall.VideoCallActivity;
import com.example.phoneapp.view.voicecall.VoiceCallActivity;
import com.example.phoneapp.viewmodel.FirebaseServiceViewModel;
import com.example.phoneapp.viewmodel.RtcViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, ItemClickListener {
    FirebaseServiceViewModel viewModel = null;
    RtcViewModel rtcViewModel;
    EditText UserNameEt;
    TextView OKButton;
    //adapter for Recyclerview
    UserRecViewAdapter userRecViewAdapter;
    //Recyclerview
    RecyclerView recyclerView;
    public static Context context;
    private static final String[] Permissions={
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserNameEt = findViewById(R.id.UserNameET);
        OKButton = findViewById(R.id.OKButton);
        OKButton.setOnClickListener(this);
        UserNameEt.setVisibility(View.INVISIBLE);
        OKButton.setVisibility(View.INVISIBLE);
        recyclerView = findViewById(R.id.RecView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        userRecViewAdapter = new UserRecViewAdapter(this, this);
        recyclerView.setAdapter(userRecViewAdapter);
        viewModel = new FirebaseServiceViewModel(Singleton.getInstance(this).repository);
        rtcViewModel = new RtcViewModel();
        //get users from FireStore db
        viewModel.FetchUsers();
        requestPermissions();
        /*
        as users are fetch from Server. setup arraylist in adapter.
         */
        viewModel.getUsersArray().observe(this, new Observer<ArrayList<UserModel>>() {
            @Override
            public void onChanged(ArrayList<UserModel> userModels) {
                userRecViewAdapter.SetUpItems(userModels);
            }
        });

         /*
         Create the observer which updates the UI as token is set.
         I want to get the token and save to server with input name here.
         */
        final Observer<String> observeToken = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d("s->", s);
                UserNameEt.setVisibility(View.VISIBLE);
                OKButton.setVisibility(View.VISIBLE);
            }
        };
        viewModel.getToken().observe(this, observeToken);
    }
    private void requestPermissions()
    {
        if (ContextCompat.checkSelfPermission(this,Permissions[0]) == PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, Permissions, 100);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.OKButton) {
            String username = UserNameEt.getText().toString();
            if (username.length() > 0) {
                viewModel.UpdateTokenAtServer(username);
                UserNameEt.setVisibility(View.INVISIBLE);
                OKButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void ItemClickedAt(int position, int RequestCode, UserModel userModel) {
        switch (RequestCode) {
            case 1:
                Log.d("1->", "make voice call");
                viewModel.SendNotificationToUser(new NotificationModel(userModel.Token, this, "voicecall"));
                JumpVoiceCallActivity();
                break;
            case 2:
                Log.d("1->", "make video call");
                Singleton.Token = userModel.Token;
                JumpVideoCallActivity();
                //viewModel.SendNotificationToUser(new NotificationModel(userModel.Token,this,"videocall"));
                break;
            case 3:
                Log.d("1->", "message");
                viewModel.SendNotificationToUser(new NotificationModel(userModel.Token,
                        this,
                        "message"));
                break;
            default:
                break;
        }
    }
    private void JumpVideoCallActivity() {
        Intent intent = new Intent(this, VideoCallActivity.class);
        startActivity(intent);
    }
    private void JumpVoiceCallActivity() {
        Intent intent = new Intent(this, VoiceCallActivity.class);
        startActivity(intent);
    }
    public static class SingleUploader {
        private static SingleUploader Minstance;
        private RequestQueue requestQueue;
        private static Context context;

        public SingleUploader(Context cntxt) {
            context = cntxt;
            requestQueue = getRequestQueue();
        }

        public RequestQueue getRequestQueue() {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(context.getApplicationContext());
            }
            return requestQueue;
        }

        public static synchronized SingleUploader getInstance(Context Ctxt) {
            if (Minstance == null) {
                Minstance = new SingleUploader(Ctxt);
            }
            return Minstance;
        }

        public <T> void addToRequestQueue(Request<T> request) {
            requestQueue.add(request);
        }
    }
}
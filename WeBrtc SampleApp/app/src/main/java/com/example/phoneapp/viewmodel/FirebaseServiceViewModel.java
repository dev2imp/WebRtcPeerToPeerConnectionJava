package com.example.phoneapp.viewmodel;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.phoneapp.model.FirebaseServiceRepository;
import com.example.phoneapp.model.NotificationModel;
import com.example.phoneapp.model.UserModel;
import java.util.ArrayList;
public class FirebaseServiceViewModel extends ViewModel {
    FirebaseServiceRepository repository =null;
    public FirebaseServiceViewModel(FirebaseServiceRepository repository) {
        this.repository = repository;
    }
    public LiveData<String> getToken(){
        if(repository != null){
            if(repository.Token!=null){
                Log.d("repositoryTokern", repository.Token.toString());
            }
           return repository.Token;
        }
        return null;
    }
    /*
    we dont want our UI to be interrupted by work that is not releated to
    UI directly, that is why A thread is started to the the process.
     */
    public void TokenHasChanged(String token){
       Runnable background = new  Runnable(){
            public void run(){
                repository.TokenHasChanged(token);
            }
        };
       new Thread(background).start();
    }
    public void UpdateTokenAtServer(String username){
        Runnable background = new  Runnable(){
            public void run(){
                repository.UpdateTokenAtServer(username);
            }
        };
        new Thread(background).start();
    }
    public LiveData<ArrayList<UserModel>> getUsersArray(){
        if(repository!=null){
            return repository.UsersArray;
        }
      return null;
    }
    //get all user from Server
    public void FetchUsers(){
        Runnable fecth = new  Runnable(){
            public void run(){
                repository.FetchUsers();
            }
        };
        new Thread(fecth).start();
    }
    public void SendNotificationToUser(NotificationModel notificationModel){
        Runnable fecth = new  Runnable(){
            public void run(){
                repository.SendNotificaitonToUser(notificationModel);
            }
        };
        new Thread(fecth).start();
    }
}

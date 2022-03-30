package com.example.phoneapp.view.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phoneapp.R;
import com.example.phoneapp.model.UserModel;

import java.util.ArrayList;

public class UserRecViewAdapter extends RecyclerView.Adapter<UserRecViewAdapter.ViewHolder>{
    Context context;
    ItemClickListener itemClickListener;
    ArrayList<UserModel> Items=new ArrayList();
    private ViewHolder holder;
    private int position;

    public UserRecViewAdapter(Context context, ItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.items_user,parent,false);
        ViewHolder viewHolder=new ViewHolder(view,itemClickListener);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.UserName.setText(Items.get(position).username);
        holder.VoiceCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.Listener.ItemClickedAt(position,1,Items.get(position));
            }
        });
        holder.VideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.Listener.ItemClickedAt(position,2,Items.get(position));
            }
        });
        holder.Mesage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.Listener.ItemClickedAt(position,3,Items.get(position));
            }
        });
    }
    @Override
    public int getItemCount() {
        return Items.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView UserName;
        ImageView VoiceCall;
        ImageView VideoCall;
        ImageView Mesage;
        ItemClickListener Listener;
        public ViewHolder(@NonNull View itemView,ItemClickListener itemClickListener) {
            super(itemView);
            UserName = itemView.findViewById(R.id.UserName);
            VoiceCall = itemView.findViewById(R.id.VoiceCall);
            VideoCall = itemView.findViewById(R.id.VideoCall);
            Mesage = itemView.findViewById(R.id.Message);
            Listener=itemClickListener;
        }
    }
    /*
    as we have  grabbed data from server
     */

   public void SetUpItems(ArrayList<UserModel> arr){
        Items=arr;
        Log.d("SetUpItems->", String.valueOf(arr.size()));
        notifyDataSetChanged();
    }
}

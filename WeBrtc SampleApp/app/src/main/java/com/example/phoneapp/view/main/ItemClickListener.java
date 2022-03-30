package com.example.phoneapp.view.main;

import com.example.phoneapp.model.UserModel;
public interface ItemClickListener {
    void ItemClickedAt(int position, int RequestCode, UserModel userModel);
}

package com.example.snapchatclone.RecyclerViewFollow;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.snapchatclone.R;

//this class holds each child of the recycler view
public class FollowViewHolders extends RecyclerView.ViewHolder {
    public TextView mEmail;
    public Button mFollow;

    public FollowViewHolders(View itemView){
        super(itemView);
        mEmail = itemView.findViewById(R.id.email);
        mFollow = itemView.findViewById(R.id.follow);
    }
}

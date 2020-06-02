package com.example.snapchatclone.RecyclerViewReceiver;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.snapchatclone.R;

//this class holds each child of the recycler view
public class ReceiverViewHolders extends RecyclerView.ViewHolder {
    public TextView mEmail;
    public CheckBox mReceive;

    public ReceiverViewHolders(View itemView){
        super(itemView);
        mEmail = itemView.findViewById(R.id.email);
        mReceive = itemView.findViewById(R.id.receive);
    }
}

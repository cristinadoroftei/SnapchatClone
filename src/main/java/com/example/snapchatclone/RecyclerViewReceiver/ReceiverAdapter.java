package com.example.snapchatclone.RecyclerViewReceiver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.snapchatclone.R;
import com.example.snapchatclone.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

//this class connects the database with the app
public class ReceiverAdapter extends RecyclerView.Adapter<ReceiverViewHolders> {

    private List<ReceiverObject> usersList;
    private Context context;

    //we use the Context in the constructor just in case we want to move to another page
    public ReceiverAdapter(List<ReceiverObject> usersList, Context context){
        this.usersList = usersList;
        this.context = context;
    }

    @Override
    // inflates the row layout from xml when needed
    public ReceiverViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_receiver_item, null);
        ReceiverViewHolders rcv = new ReceiverViewHolders(layoutView);
        return rcv;
    }

    @Override
    // binds the data to the TextView in each row
    public void onBindViewHolder(final ReceiverViewHolders holder, int position) {
        holder.mEmail.setText(usersList.get(position).getEmail());
        holder.mReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this will turn the true to false and vice versa
                boolean receiveState = !usersList.get(holder.getLayoutPosition()).getReceive();
                usersList.get(holder.getLayoutPosition()).setReceive(receiveState);
            }
        });
    }

    @Override
    // total number of rows
    public int getItemCount() {
        return this.usersList.size();
    }
}

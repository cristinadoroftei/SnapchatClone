package com.example.snapchatclone.RecyclerViewFollow;

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
public class FollowAdapter extends RecyclerView.Adapter<FollowViewHolders> {

    private List<FollowObject> usersList;
    private Context context;

    //we use the Context in the constructor just in case we want to move to another page
    public FollowAdapter(List<FollowObject> usersList, Context context){
        this.usersList = usersList;
        this.context = context;
    }

    @Override
    // inflates the row layout from xml when needed
    public FollowViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_followers_item, null);
        FollowViewHolders rcv = new FollowViewHolders(layoutView);
        return rcv;
    }

    @Override
    // binds the data to the TextView in each row
    public void onBindViewHolder(final FollowViewHolders holder, int position) {
        holder.mEmail.setText(usersList.get(position).getEmail());
        if(UserInformation.listFollowing.contains(usersList.get(holder.getLayoutPosition()).getUid())){
            holder.mFollow.setText("following");
        } else {
            holder.mFollow.setText("follow");
        }

        holder.mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usersList.get(holder.getLayoutPosition()).getUid()==null){return;}
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (!UserInformation.listFollowing.contains(usersList.get(holder.getLayoutPosition()).getUid())){
                    holder.mFollow.setText("following");
                    FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("following").child(usersList.get(holder.getLayoutPosition()).getUid()).setValue(true);

                } else {
                    holder.mFollow.setText("follow");
                    FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("following").child(usersList.get(holder.getLayoutPosition()).getUid()).removeValue();

                }
            }
        });
    }

    @Override
    // total number of rows
    public int getItemCount() {
        return this.usersList.size();
    }
}

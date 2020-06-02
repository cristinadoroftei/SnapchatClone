package com.example.snapchatclone.RecyclerViewStory;

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
public class StoryAdapter extends RecyclerView.Adapter<StoryViewHolders> {

    private List<StoryObject> storiesList;
    private Context context;

    //we use the Context in the constructor just in case we want to move to another page
    public StoryAdapter(List<StoryObject> usersList, Context context){
        this.storiesList = usersList;
        this.context = context;
    }

    @Override
    // inflates the row layout from xml when needed
    public StoryViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_story_item, null);
        StoryViewHolders rcv = new StoryViewHolders(layoutView);
        return rcv;
    }

    @Override
    // binds the data to the TextView in each row
    public void onBindViewHolder(final StoryViewHolders holder, int position) {
        holder.mEmail.setText(storiesList.get(position).getEmail());
        holder.mEmail.setTag(storiesList.get(position).getUid());

        holder.mLayout.setTag(storiesList.get(position).getChatOrStory());
    }

    @Override
    // total number of rows
    public int getItemCount() {
        return this.storiesList.size();
    }
}

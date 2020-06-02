package com.example.snapchatclone.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snapchatclone.R;

import com.example.snapchatclone.RecyclerViewStory.StoryAdapter;
import com.example.snapchatclone.RecyclerViewStory.StoryObject;
import com.example.snapchatclone.UserInformation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class StoryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<StoryObject> result = new ArrayList<>();

    public static StoryFragment newInstance(){
        StoryFragment fragment = new StoryFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        //inflate and return the view that we want
        View view = inflater.inflate(R.layout.fragment_story , container, false);

        mRecyclerView = view.findViewById(R.id.recyclerview);

        //allows us to have a fluid movement throughout the recycler view
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //the getDataSet() method will return the list of the users, which is needed in the constructor for the adapter
        mAdapter = new StoryAdapter(getDataSet(), getContext());


        mRecyclerView.setAdapter(mAdapter);

        Button mRefresh = view.findViewById(R.id.refresh);
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
                listenForData();
            }
        });

        return view;
    }

    private void clear() {
        int size = this.result.size();
        this.result.clear();
        mAdapter.notifyItemRangeChanged(0, size);
    }

    private ArrayList<StoryObject> getDataSet() {
        listenForData();
        return result;
    }

    private void listenForData() {
        for (int i = 0; i< UserInformation.listFollowing.size(); i++){
            DatabaseReference followingStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserInformation.listFollowing.get(i));
            followingStoryDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String email = dataSnapshot.child("email").getValue().toString();
                    String uid = dataSnapshot.getRef().getKey();
                    long timestampBeg = 0;
                    long timestampEnd = 0;
                    //loop through the child "story" and get all the stories that are inside of it
                    for (DataSnapshot storysnapshot: dataSnapshot.child("story").getChildren()){
                        if(storysnapshot.child("timeStampBeg").getValue() != null){
                            timestampBeg = Long.parseLong(storysnapshot.child("timeStampBeg").getValue().toString());
                        }
                        if(storysnapshot.child("timeStampEnd").getValue() != null){
                            timestampEnd = Long.parseLong(storysnapshot.child("timeStampEnd").getValue().toString());
                        }

                        long timestampCurrent = System.currentTimeMillis();
                        if (timestampCurrent >= timestampBeg && timestampCurrent<= timestampEnd){
                            //populating the recycler view
                            StoryObject obj = new StoryObject(email, uid, "story");
                            //if the user does not have any story in the last 24 hours, than we can add it to the recycler view
                            if(!result.contains(obj)){
                                result.add(obj);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private ArrayList<StoryObject> result = new ArrayList<>();

    public static ChatFragment newInstance(){
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        //inflate and return the view that we want
        View view = inflater.inflate(R.layout.fragment_chat , container, false);

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

    private void listenForData(){
        //get the uid of the user that sent a story to the current user
        DatabaseReference receiveDb = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("received");
        receiveDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        //the snapshot.getKey() returns the id of each user
                        getUserInfo(snapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserInfo(String key) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("users").child(key);
        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String email = dataSnapshot.child("email").getValue().toString();
                    String uid = dataSnapshot.getRef().getKey();
                    StoryObject obj = new StoryObject(email,uid,"chat");
                    if (!result.contains(obj)){
                        result.add(obj);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

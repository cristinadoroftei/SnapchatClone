package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.snapchatclone.RecyclerViewFollow.FollowAdapter;
import com.example.snapchatclone.RecyclerViewFollow.FollowObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class FindUsersActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<FollowObject> result = new ArrayList<>();


    EditText mInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_users);

        mInput = findViewById(R.id.searchInput);
        Button mSearchButton = findViewById(R.id.search);

        mRecyclerView = findViewById(R.id.recyclerview);

        //allows us to have a fluid movement throughout the recycler view
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //the getDataSet() method will return the list of the users, which is needed in the constructor for the adapter
        mAdapter = new FollowAdapter(getDataSet(), getApplication());


        mRecyclerView.setAdapter(mAdapter);

        //onClickListener that will show the usersList that the current user will most likely want to follow
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
                listenForData();
            }
        });
    }



    private void clear() {
        int size = this.result.size();
        this.result.clear();
        mAdapter.notifyItemRangeChanged(0, size);
    }

    //search for the names that we want
    private void listenForData() {
        DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("users");
        //order all the users by email and only find the ones that start with the mInput text and also the ones that end with the mInput text
        Query query = usersDb.orderByChild("email").startAt(mInput.getText().toString()).endAt(mInput.getText().toString() + "\uf8ff");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded( DataSnapshot dataSnapshot, String s) {
                String email = "";
                String uid = dataSnapshot.getRef().getKey();
                if(dataSnapshot.child("email").getValue() != null){
                    email = dataSnapshot.child("email").getValue().toString();
                }

                if(!email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                    FollowObject obj = new FollowObject(email, uid);
                    result.add(obj);
                    //notify the adapter that something in the RecyclerView has changed
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private ArrayList<FollowObject> getDataSet() {
        listenForData();
        return result;
    }
}

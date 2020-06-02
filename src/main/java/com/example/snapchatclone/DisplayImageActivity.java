package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.snapchatclone.RecyclerViewStory.StoryObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DisplayImageActivity extends AppCompatActivity {

    String userId, chatOrStory;
    private ImageView mImage;

    //a flag that indicates whether the user loaded an image
    private boolean started = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        //get the bundle from the intent from StoryViewHolders class
        Bundle b = getIntent().getExtras();
        userId = b.getString("userId");
        chatOrStory = b.getString("chatOrStory");

        mImage = findViewById(R.id.image);

        switch (chatOrStory){
            case "chat":
                listenForChat();
                break;
            case "story":
                listenForStory();
        }


    }

    private void listenForChat() {
        final DatabaseReference chatDb = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("received").child(userId);
        chatDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageUrl = "";
                //loop through the child in the "received" for a specific user and get all the stories that are inside of it
                for (DataSnapshot chatsnapshot: dataSnapshot.getChildren()){
                    if(chatsnapshot.child("imageUrl").getValue() != null){
                        imageUrl = chatsnapshot.child("imageUrl").getValue().toString();
                    }
                        imageUrlList.add(imageUrl);
                        if (!started){
                            started = true;
                            initializeDisplay();
                        }
                    chatDb.child(chatsnapshot.getKey()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    ArrayList<String> imageUrlList = new ArrayList<>();
    private void listenForStory() {

            DatabaseReference followingStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            followingStoryDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String imageUrl = "";
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
                        if(storysnapshot.child("imageUrl").getValue() != null){
                            imageUrl = storysnapshot.child("imageUrl").getValue().toString();
                        }

                        long timestampCurrent = System.currentTimeMillis();
                        if (timestampCurrent >= timestampBeg && timestampCurrent<= timestampEnd){
                            imageUrlList.add(imageUrl);
                            if (!started){
                                started = true;
                                initializeDisplay();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //this attirbute is indicating in which position of our imageUrls we are in
        private int imageIterator = 0;
    private void initializeDisplay() {
        //Glide is a library used for loading images from an url
        //this line is basically loading the image url into the ImageView
        Glide.with(getApplication()).load(imageUrlList.get(imageIterator)).into(mImage);

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage();
            }
        });
        final Handler handler = new Handler();
        final int delay = 5000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //this 2 lines make sure that the changeImage() will be called again and again until we reach the last imageUrl from the stories
                changeImage();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void changeImage() {
        //if the last image is loaded, end the activity, otherwise continue by diplaying the next one
        if (imageIterator == imageUrlList.size()-1){
            finish();
            return;
        }
        imageIterator++;
        Glide.with(getApplication()).load(imageUrlList.get(imageIterator)).into(mImage);
    }
}

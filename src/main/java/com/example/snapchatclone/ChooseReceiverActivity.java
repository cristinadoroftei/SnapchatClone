package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.snapchatclone.RecyclerViewFollow.FollowObject;
import com.example.snapchatclone.RecyclerViewReceiver.ReceiverAdapter;
import com.example.snapchatclone.RecyclerViewReceiver.ReceiverObject;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseReceiverActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    EditText mInput;

    String Uid;
    Bitmap rotateBitmap;

    private ArrayList<ReceiverObject> result = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_receiver);

        //gets the file path
        String filePath=getIntent().getStringExtra("path");

        //loads the file
        File file = new File(filePath);

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        rotateBitmap = rotate(bitmap);

        //the unique id of the user
        Uid = FirebaseAuth.getInstance().getUid();

        mInput = findViewById(R.id.searchInput);
        Button mSearchButton = findViewById(R.id.search);

        mRecyclerView = findViewById(R.id.recyclerView);

        //allows us to have a fluid movement throughout the recycler view
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //the getDataSet() method will return the list of the users, which is needed in the constructor for the adapter
        mAdapter = new ReceiverAdapter(getDataSet(), getApplication());

        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToStories();
            }
        });
    }


    private ArrayList<ReceiverObject> getDataSet() {
        listenForData();
        return result;
    }

    private void listenForData(){
        for(int i=0; i< UserInformation.listFollowing.size(); i++){
            DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserInformation.listFollowing.get(i));
            usersDb.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String email = "";
                    String uid = dataSnapshot.getRef().getKey();
                    if(dataSnapshot.child("email").getValue() != null){
                        email = dataSnapshot.child("email").getValue().toString();
                    }
                    ReceiverObject obj = new ReceiverObject(email, uid, false);
                    if(!result.contains(obj)){
                        result.add(obj);
                        mAdapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }


    private void saveToStories() {

        //go to the "story" folder of the logged in user
        final DatabaseReference userStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(Uid).child("story");
        //this will give us an available key that we can use freely
        final String key = userStoryDb.push().getKey();

        //the filePath is the image path within the storage unit.
        //this image will be stored in a folder called "captures" and it will have a name "key", which is the unique id that we got from firebase
        StorageReference filePath = FirebaseStorage.getInstance().getReference().child("captures").child(key);

        //converting the image back to a byte array, because this is what firebase accepts
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] dataToUpload = baos.toByteArray();

        //upload the image to the database
        UploadTask uploadTask = filePath.putBytes(dataToUpload);


        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //the the url that is associated with the image
                Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();

                        //get the time when the image is uploaded and the time when it should disappear
                        Long currentTimeStamp = System.currentTimeMillis();
                        Long endTimeStamp = currentTimeStamp + (24*60*60*1000);

                        CheckBox mStory = findViewById(R.id.story);
                        if(mStory.isChecked()){
                            //create a hashmap with all the data we need to upload
                            Map<String, Object> mapToUpload = new HashMap<>();
                            mapToUpload.put("imageUrl", imageUrl);
                            mapToUpload.put("timeStampBeg", currentTimeStamp);
                            mapToUpload.put("timeStampEnd", endTimeStamp);
                            //upload the image to the database
                            userStoryDb.child(key).setValue(mapToUpload);
                        }

                        for (int i =0; i< result.size(); i++){
                            if (result.get(i).getReceive() == true){
                                DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("users").child(result.get(i).getUid()).child("received").child(Uid);
                                //create a hashmap with all the data we need to upload
                                Map<String, Object> mapToUpload = new HashMap<>();
                                mapToUpload.put("imageUrl", imageUrl);
                                mapToUpload.put("timeStampBeg", currentTimeStamp);
                                mapToUpload.put("timeStampEnd", endTimeStamp);
                                //upload the image to the database
                                userDb.child(key).setValue(mapToUpload);
                            }
                        }

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        return;
                    }
                });
            }
        });

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                finish();
                return;
            }
        });


    }

    private Bitmap rotate(Bitmap decodedBitmap){
        int width = decodedBitmap.getWidth();
        int height = decodedBitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(90);

        return Bitmap.createBitmap(decodedBitmap, 0, 0, width, height, matrix, true);
    }
}

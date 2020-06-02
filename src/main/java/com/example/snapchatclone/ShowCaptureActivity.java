package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ShowCaptureActivity extends AppCompatActivity {

    String Uid;
    Bitmap rotateBitmap;
    String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_capture);
        ImageView imageView = findViewById(R.id.imageCaptured);

        //gets the file path
        filePath=getIntent().getStringExtra("path");

        //loads the file
        File file = new File(filePath);

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        rotateBitmap = rotate(bitmap);

        imageView.setImageBitmap(rotateBitmap);

        //the unique id of the user
        Uid = FirebaseAuth.getInstance().getUid();

        Button mSend = findViewById(R.id.send);


        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), ChooseReceiverActivity.class);
                //passes the file path string with the intent
                intent.putExtra("path", filePath);
                startActivity(intent);
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

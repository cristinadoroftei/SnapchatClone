package com.example.snapchatclone.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.snapchatclone.FindUsersActivity;
import com.example.snapchatclone.R;
import com.example.snapchatclone.ShowCaptureActivity;
import com.example.snapchatclone.loginRegistration.SplashScreenActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


public class CameraFragment extends Fragment implements SurfaceHolder.Callback{

    Camera camera;

    //the only variable for capturing the camera
    Camera.PictureCallback jpegCallback;

    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;

    final int CAMERA_REQUEST_CODE = 1;

    public static CameraFragment newInstance(){
        CameraFragment fragment = new CameraFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        //inflate and return the view that we want
        View view = inflater.inflate(R.layout.fragment_camera , container, false);

        mSurfaceView = view.findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();

        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else{
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        Button mLogout = view.findViewById(R.id.logout);
        Button mCapture = view.findViewById(R.id.capture);
        Button mFindUsers = view.findViewById(R.id.findUsers);

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
        mFindUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findUsers();
            }
        });
        mCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        jpegCallback = new Camera.PictureCallback(){
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap decodeBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                //creates the temporary file and gets the path
                String filePath= tempFileImage(getActivity(),decodeBitmap,"name");
                Intent intent = new Intent(getActivity(), ShowCaptureActivity.class);

                //passes the file path string with the intent
                intent.putExtra("path", filePath);

                startActivity(intent);

            }
        };

        return view;
    }


    //creates a temporary file and return the absolute file path
    public static String tempFileImage(Context context, Bitmap bitmap, String name) {

        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Error writing file", e);
        }

        return imageFile.getAbsolutePath();
    }

    private void captureImage() {
        //the jpegCallback receives the picture that was captured
        camera.takePicture(null, null, jpegCallback);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();

        Camera.Parameters parameters;
        parameters = camera.getParameters();

        camera.setDisplayOrientation(90);
        parameters.setPreviewFrameRate(30);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        //fix the size of the preview, because before the image looked quite weird
        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
        bestSize = sizeList.get(0);
        //loop through all the possible good sizes and choose one which can possibly match our best image view case,
        //in our case the biggest one, because we use full screen
        for (int i = 1; i< sizeList.size(); i++){
            if ((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)){
                bestSize = sizeList.get(i);
            }
        }
        parameters.setPreviewSize(bestSize.width, bestSize.height);


        camera.setParameters(parameters);
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                //if the result of the camera permission request is positive
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mSurfaceHolder.addCallback(this);
                    mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                } else {
                    Toast.makeText(getContext(), "Please provide the camera permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void findUsers() {
        Intent intent = new Intent(getContext(), FindUsersActivity.class);
        startActivity(intent);
        return;
    }

    private void logOut(){
        FirebaseAuth.getInstance().signOut();
        //getContext() will not retrieve the Camera Fragment, it will retrieve the MainActivity.
        //we switch to the SplashScreen in order to show the activity that belongs to a user that is NOT logged in
        Intent intent = new Intent(getContext(), SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return;
    }
}

package com.example.snapchatclone.loginRegistration;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.snapchatclone.MainActivity;
import com.example.snapchatclone.loginRegistration.ChooseLoginRegistrationActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    public static Boolean started = false;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this variable contains all the information about the user that is logged in (id, username, age, etc)
        mAuth = FirebaseAuth.getInstance();

        //if a user is already logged in
        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(getApplication(), MainActivity.class);
            //if the user is logging out and logging in again, erase what he has previously done
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        } else{
            Intent intent = new Intent(getApplication(), ChooseLoginRegistrationActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }
}

package com.example.snapchatclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.snapchatclone.fragment.CameraFragment;
import com.example.snapchatclone.fragment.ChatFragment;
import com.example.snapchatclone.fragment.StoryFragment;

public class MainActivity extends AppCompatActivity {

    //this variable connects the adapter with the main activity
    FragmentPagerAdapter adapterViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserInformation userInformationListener = new UserInformation();
        userInformationListener.startFetching();

        ViewPager viewPager = findViewById(R.id.viewPager);

        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        //because we want the camera fragment to appear first, because this is how it is on snapchat, we will set the current item
        //to 1, which is case 1 in the Fragment class
        viewPager.setCurrentItem(1);


    }

    //the adapter is the class "controlling" the Main activity
    public static class MyPagerAdapter extends FragmentPagerAdapter{


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        //returns (depending on the position of the fragmnets) the fragment that we need
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return ChatFragment.newInstance();
                case 1:
                    return  CameraFragment.newInstance();
                case 2:
                    return StoryFragment.newInstance();
            }
            return null;
        }

        @Override
        //returns the number of pages that we have within our viewPager, in our case: 3 (chat, camera, stories)
        public int getCount() {
            return 3;
        }
    }
}

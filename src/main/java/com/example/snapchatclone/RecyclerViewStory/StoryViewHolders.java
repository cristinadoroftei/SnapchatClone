package com.example.snapchatclone.RecyclerViewStory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.snapchatclone.DisplayImageActivity;
import com.example.snapchatclone.R;

//this class holds each child of the recycler view
public class StoryViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView mEmail;
    public LinearLayout mLayout;

    public StoryViewHolders(View itemView){
        super(itemView);
        //this on click listener is linked to the onClick method below
        itemView.setOnClickListener(this);
        mEmail = itemView.findViewById(R.id.email);
        mLayout = itemView.findViewById(R.id.layout);
    }

    //every time the user clicks on a view holder (aka item) of the recycler view,
    //the onClick method will be called
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), DisplayImageActivity.class);
        Bundle b = new Bundle();
        //get the uid of the recycler view item that we just clicked on
        b.putString("userId", mEmail.getTag().toString());
        b.putString("chatOrStory", mLayout.getTag().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);

    }


}

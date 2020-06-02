package com.example.snapchatclone.RecyclerViewFollow;

public class FollowObject {

    private String email;
    private String uid;

    public FollowObject(String email, String uid){
        this.email = email;
        this.uid = uid;
    }

    public String getUid(){
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}

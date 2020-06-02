package com.example.snapchatclone.RecyclerViewReceiver;

public class ReceiverObject {

    //everytime an user click a checkbox, then the receive value will change to true or false
    private boolean receive;
    private String email;
    private String uid;

    public ReceiverObject(String email, String uid, boolean receive){
        this.email = email;
        this.uid = uid;
        this.receive = receive;
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

    public Boolean getReceive(){
        return this.receive;
    }

    public void setReceive(Boolean receive) {
        this.receive = receive;
    }

}

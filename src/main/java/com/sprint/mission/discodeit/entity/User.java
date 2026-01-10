package com.sprint.mission.discodeit.entity;


import java.util.List;

public class User extends DiscodeEntity {

    private String userName;
    private String email;
    private String userId;
    private List<Channel> channels;

    public User(String email, String userName, String userId){
        this.userName = userName;
        this.email = email;
        this.userId = userId;
        updateTime();
    }

    public String getUserName(){
        return this.userName;
    }

    public String getUserEmail(){
        return this.email;
    }

    public String getUserId(){
        return this.userId;
    }

    public void updateName(String name){
        this.userName = name;
        updateTime();
    }

    public void updateEmail(String email){
        this.email = email;
        updateTime();
    }

    public void updateUserId(String userId){
        this.userId = userId;
        updateTime();
    }

    public String toString(){
        return "UserID: " + this.userId + " / Email: " + this.email + " / userName: " + this.userName;
    }

}

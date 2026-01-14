package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends BaseEntity {
    private String name;
    private String intro;
    private List <User> userList = new ArrayList<>();
    private List <Message> messageList = new ArrayList<>();



    public Channel(String name, String intro) {
        super();
        this.name = name;
        this.intro = intro;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setUpdatedAt(System.currentTimeMillis());
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro){
        this.intro = intro;
        setUpdatedAt(System.currentTimeMillis());
    }

    public int getCurrentUserCount(){
        return userList.size();
    }

    public void enter(User user) {
        userList.add(user);
        setUpdatedAt(System.currentTimeMillis());
    }

    public void exit(User user) {
        userList.remove(user);
        setUpdatedAt(System.currentTimeMillis());
    }

    public List<User> getUserList() {
        return userList;
    }

    public void addMessage(Message message){
        messageList.add(message);
        setUpdatedAt(System.currentTimeMillis());
    }

    public void removeMessage(Message message){
        messageList.remove(message);
        setUpdatedAt(System.currentTimeMillis());
    }

    public int getMessageCount(){
        return messageList.size();
    }


}

package com.sprint.mission.discodeit.entity;


import java.util.*;

public class User extends DiscordEntity {

    private String userName;
    private String email;
    private String userId;
    private List<Channel> channelList; // 유저가 현재 가입되어 있는 채널의 목록
    private List<Message> messageList;

    public User(String userName, String email, String userId){
        this.userName = userName;
        this.email = email;
        this.userId = userId;
        this.channelList = new ArrayList<>();
        this.messageList = new ArrayList<>();
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

    public List<Channel> getChannelList(){
        return this.channelList;
    }

    public void updateName(String name){
        this.userName = name;
        updateTime();
    }


    public void updateEmail(String email){
        this.email = email;
        updateTime();
    }

    public List<Message> getMessageList(){
        System.out.printf("%s 이 작성하신 메세지 목록입니다.%n %s %n", this.userId, this.messageList);
        return this.messageList;
    }

    public void addMsg(Message msg){
            this.messageList.add(msg);
            updateTime();
        }

    public void deleteMsg(Message msg){
           this.messageList.remove(msg);
           updateTime();
        }

    public String toString(){
        return String.format("[User] userId: %s | username: %s | email: %s", this.userId, this.userName, this.email);
    }

}

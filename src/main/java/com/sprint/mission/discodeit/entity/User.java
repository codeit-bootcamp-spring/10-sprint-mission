package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class User extends Common{
    private String userName;
    private String email;
    private List<Channel> channelList;
    private List<Message> messageList;

    public User(String userName, String email){
        super();
        this.userName = userName;
        this.email = email;
        this.channelList = new ArrayList<>();
        this.messageList = new ArrayList<>();
    }

    public String getUserName(){
        return userName;
    }

    public void setUserName(String userName){
        this.userName = userName;
        setUpdatedAt();
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
        setUpdatedAt();
    }


    // 채널, 메세지 부분
    // 상호참조
    public List<Channel> getChannelList(){
        return channelList;
    }

    public void addChannel(Channel channel){
        this.channelList.add(channel);
        if(!channel.getUserList().contains(this)) {
            channel.addUserList(this);
        }
    }

    public List<Message> getMessageList(){
        return messageList;
    }

    public void addMessage(Message message){
        this.messageList.add(message);
    }

}

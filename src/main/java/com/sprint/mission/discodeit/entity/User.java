package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends Common implements Serializable {

    private String userName;
    private String email;
    private String status;
    private List<Channel> channelList;
    private List<Message> messageList;

    public User(String userName, String email, String status){
        //super가 숨겨져있음.
        this.userName = userName;
        this.email = email;
        this.status = status;
        this.channelList = new ArrayList<>();
        this.messageList = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        setUpdatedAt();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Channel> getChannelList(){
        return channelList;
    }

    public List<Message> getMessageList(){
        return messageList;
    }

    @Override
    public String toString() {
        return String.format(
                "[유저명:%s]",
                userName

        );
    }

    public void addChannel(Channel channel){
        if(channelList.contains(channel)){
            throw new IllegalArgumentException("이미 채널이 존재합니다.");
        }
        channelList.add(channel);
    }

    public void removeChannel(Channel channel){
        if (!channelList.contains(channel)) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다.");
        }
        channelList.remove(channel);
    }


}

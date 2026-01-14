package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel extends Common{

    private String channelName;
    private String type;
    private User owner;
    private List<User> userList;
    private List<Message> messageList;


    public Channel(String channelName, String type, User owner){
        //super();
        this.channelName = channelName;
        this.type = type;
        this.owner = owner; //채널의 소유주
        this.userList = new ArrayList<>();
        this.userList.add(owner); // 소유자는 기본 참여자;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getType() {
        return type;
    }

    public User getUser(){
        return owner;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUser(User user){
        this.owner = user;
    }


    public List<User> getUserList(){
        return userList;
    }

    @Override
    public String toString() {
        return String.format(
                "User{id=%s, channelName='%s', type='%s', ownerId='%s', 생성일자=%d, 수정일자=%d}",
                getId(),
                channelName,
                type,
                owner.getId(),
                getCreatedAt(),
                getUpdatedAt()
        );
    }

    public void enter(User user){
        if(userList.contains(user)){
            throw new IllegalArgumentException("이미 참여하고 있는 사용자입니다.");
        }
        userList.add(user);

    }

    public void leave(User user){
        if(user.equals(owner)){
            throw new IllegalArgumentException("방장은 퇴장할 수 없습니다.");
        }
        if(!(userList.contains(user))){
            throw new IllegalArgumentException(user.getUserName() + "가 채널에 없습니다.");
        }
        userList.remove(user);
    }
}
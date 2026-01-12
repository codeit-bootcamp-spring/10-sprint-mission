package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class User extends BaseEntity {

    private List<Channel> channels;
    //private String nickName;
    private String userName;
    private String email;
    //private String phoneNumber;
    private String password;

    public User(String userName,String email, String password) {
        super();
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.channels = new ArrayList<>();
    }

    public String getEmail() { return email;}
    public List<Channel> getChannels() { return channels; }
    public String getUserName() { return userName;}
    public void setEmail(String email) {
        this.email = email;
    }
    public void setUserName(String userName) {
        this.userName = userName;
        setUpdatedAt();
    }
//    public void setPhoneNumber(String phoneNumber) {
//        this.phoneNumber = phoneNumber;
//        setUpdatedAt();
//    }
//    public void setPassword(String password) {
//        this.password = password;
//        setUpdatedAt();
//    }

    public void addChannel(Channel channel) {
        channels.add(channel);
        if(!channel.getMembers().contains(this)) {
            channel.addMember(this);
        }
    }
    public void removeChannel(Channel channel) {
        channels.remove(channel);
        if(channel.getMembers().contains(this)) {
            channel.removeMember(this);
        }
    }
    public void removeAllChannels() {
        int channelsSize = channels.size();
        for(int i = 0; i < channelsSize; i++) {
            removeChannel(channels.get(i));
        }
    }

    @Override
    public String toString() {
        List<String> channelNames = channels.stream().map(c->c.getName()).toList();
        return "이름: " + userName
                //+ "\n별명" + nickName
                +"\n이메일"+ email
                //+ "\n전화번호: "+ phoneNumber
                +"\n참여 채널: "+channelNames
                +"\n생성: "+getCreatedAt()
                + "\n마지막 수정: "+getUpdatedAt()+"\n";
    }
}


package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Channel> channels;
    private String userName;
    private String email;
    private String password;

    public User(String userName,String email, String password) {
        super();
        validateUserName(userName);
        validateEmail(email);
        validatePassword(password);
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.channels = new ArrayList<>();
    }

    public String getEmail() { return email;}
    public List<Channel> getChannels() { return channels; }
    public String getUserName() { return userName;}
    public String getPassword() { return password; }
    public void setUserName(String userName) {
        validateUserName(userName);
        this.userName = userName;
        setUpdatedAt();
    }
    public void setEmail(String email) {
        validateEmail(email);
        this.email = email;
        setUpdatedAt();
    }
    public void setPassword(String password) {
        validatePassword(password);
        this.password = password;
        setUpdatedAt();
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
        if(!channel.getMembers().contains(this)) {
            channel.addMember(this);
        }
    }
    public void removeChannel(Channel channel) {
        channels.remove(channel);//디엠에서는 사용자가 삭제되어도 멤버에서 제거x(dm은 아이디로 대화 조회하니까)
        if(channel.getMembers().contains(this)&&channel.getChannelType()!=ChannelType.DIRECT) {
            channel.removeMember(this);
        }
    }
    public void removeAllChannels() {
        for(Channel channel: new ArrayList<>(channels)) {
            removeChannel(channel);
        }
    }

    private void validateUserName(String userName) {
        if(userName==null||userName.trim().isEmpty()){
            throw new IllegalArgumentException("사용자 이름이 null 또는 비어있음");
        }
    }
    private void validateEmail(String email) {
        if(email==null || email.trim().isEmpty()){
            throw new IllegalArgumentException("이메일이 null 또는 비어있음");
        }
    }
    private void validatePassword(String password) {
        if(password == null || password.trim().isEmpty()){
            throw new IllegalArgumentException("비밀번호가 null 또는 비어있음");
        }
    }
    @Override
    public String toString() {
        List<String> channelNames = channels.stream().map(c->c.getName()).toList();
        return "이름: " + userName
                +"\n이메일"+ email
                //+"\nid"+getId()
                +"\n참여 채널: "+channelNames
                +"\n생성: "+getCreatedAt()
                + "\n마지막 수정: "+getUpdatedAt()+"\n";
    }
}


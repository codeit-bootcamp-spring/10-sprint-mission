package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class Channel extends BaseEntity {

    public static final String DIRECT_CHANNEL = "directChannel";
    public static final String PRIVATE_CHANNEL = "privateChannel";
    public static final String PUBLIC_CHANNEL = "publicChannel";//공개: 멤버가 없음

    private String name;
    private String description;
    private User owner;
    private List<User> members;
    private String channelType;

    public Channel(String name, String description, User owner, String channelType) {
        super();
        //채널 타입 필수
        if(!channelType.equals(DIRECT_CHANNEL)){//디엠빼고는 전부 네임이 필수
            validateChannelName(name);
        }
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.members = new ArrayList<>();
        this.channelType = channelType;
        if(channelType.equals(PRIVATE_CHANNEL)) {//비공개인경우 멤버에 소유자 추가
         addMember(owner);
        }
    }

    public String getName() { return name;}
    public List<User> getMembers() {return members;}
    public User getOwner() { return owner;}
    public String getDescription() {return description;}
    public String getChannelType() { return channelType;}
    public void setName(String name) {
        validateChannelName(name);
        this.name = name;
        setUpdatedAt();
    }
    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt();
    }
    public void addMember(User user) {
        if(!members.contains(user)){
            members.add(user);
        }
        if(!user.getChannels().contains(this)) {
            user.addChannel(this);
        }
        setUpdatedAt();
    }
    public void addMembers(List<User> users) {
        for (User user : users) {
            addMember(user);
        }
    }
    public void removeMember(User user) {
        members.remove(user);
        if(user.getChannels().contains(this)) {
            user.removeChannel(this);
        }
        setUpdatedAt();
    }
    public void removeAllMembers() {
        for (User member : new ArrayList<>(members)) {
            removeMember(member);
        }
    }
    public void removeMembers(List<User> users) {
        for (User member : new ArrayList<>(users)) {
            removeMember(member);
        }
    }

    private void validateChannelName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("채널 이름이 null 또는 비어있음");
        }
    }
    public void checkChannelOwner(User user) {
        if(!owner.getId().equals(user.getId())){
            throw new IllegalArgumentException("채널의 소유자가 아님: [채널ID-"+this.getId()+" 사용자ID-" + user.getId()+"]");
        }
    }
    public void checkMember(User member) {
        if(!members.contains(member)){
            throw new IllegalArgumentException("채널에 속한 사용자가 아님: "+member.getId());
        }
    }
    @Override
    public String toString() {
        List<String> memberList = members.stream().map(m->m.getEmail()).toList();
        return "채널 이름: " + name+
                "\n설명: "+ description+
                "\n소유자: "+ owner.getEmail()
                +"\n멤버: "+memberList
                +"\n생성: " +getCreatedAt()
                +"\n마지막 수정: "+getUpdatedAt()+"\n";
    }
}

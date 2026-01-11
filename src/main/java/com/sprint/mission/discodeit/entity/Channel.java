package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    private String name;
    private String description;
    private User owner;
    private boolean openType;//공개여부 공개: 멤버가 없음
    private List<User> members;
    //private List<Message> messages;

    public Channel(String name, String description, User owner, boolean openType) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.openType = openType;
        this.members = new ArrayList<>();
        if(!openType) {//비공개인경우 멤버에 소유자 추가
         addMember(owner);
        }
        //this.messages = new ArrayList<>();
    }
    public UUID getId() {
        return id;
    }
    public String getName() { return name;}
    public List<User> getMembers() {return members;}
    public User getOwner() { return owner;}
    public boolean isOpenType() { return openType;}
//    public List<Message> getMessages() {
//        return messages;
//    }
    private void setUpdatedAt() {
        this.updatedAt =  System.currentTimeMillis();
    }
    public void setName(String name) {
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
        this.updatedAt = System.currentTimeMillis();
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
    }
    public void removeAllMembers() {
        for (User member : new ArrayList<>(members)) {
            removeMember(member);
        }
    }
    /*
    public void addMessage(Message message) {
        messages.add(message);
        if(!message.getChannel().equals(this)) {
            message.setChannel(this);
        }
    }
    public void removeMessage(Message message) {
        messages.remove(message);
        if(!message.getChannel().equals(this)) {
            message.setChannel(null);
        }
    }


     */
    @Override
    public String toString() {
        List<String> memberList = members.stream().map(m->m.getEmail()).toList();
        return "채널 이름: " + name+
                "\n설명: "+ description+
                "\n소유자: "+ owner.getEmail()
                +"\n멤버: "+memberList
                +"\n생성: " +createdAt
                +"\n마지막 수정: "+updatedAt+"\n";
    }
}

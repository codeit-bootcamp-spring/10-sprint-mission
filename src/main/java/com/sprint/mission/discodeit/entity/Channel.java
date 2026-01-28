package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.*;

public class Channel extends Base  {
    // 필드
    private String name;
    private final List<User> membersList;
    private final List<Message> messageList;

    // 생성자
    public Channel(String name) {
        super();
        this.name = name;
        this.membersList = new ArrayList<>();
        this.messageList = new ArrayList<>();
    }

    // getter
    public String getName() {
        return name;
    }

    public List<User> getMembersList() {
        return membersList;
    }

    public List<Message> getMessageList(){
        return messageList;
    }

    // setter
    public void updateName(String name) {
        this.name = name;
        updateUpdatedAt();
    }

    // other
    public void addMember(User member){
        membersList.add(member);
        updateUpdatedAt();
    }

    public void removeMember(User member) {
        membersList.remove(member);
        updateUpdatedAt();
    }

    public void addMessage(Message msg){
        messageList.add(msg);
        updateUpdatedAt();
    }

    public void removeMessage(Message msg){
        messageList.remove(msg);
        updateUpdatedAt();
    }
}

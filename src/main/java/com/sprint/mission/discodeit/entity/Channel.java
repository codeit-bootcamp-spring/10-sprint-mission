package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends Base {
    private String name;
    private final Set<User> membersSet;
    private List<Message> messageList;

    public Channel(String name) {
        super();
        this.name = name;
        this.membersSet = new HashSet<>();
        this.messageList = new ArrayList<Message>();
    }

    // getter
    public String getName() {
        return name;
    }

    public Set<User> getMembersSet() {
        return membersSet;
    }

    public List<Message> getMessageList(){
        return messageList;
    }

    // setter
    public void updateName(String name) {
        this.name = name;
    }

    // other
    public void addMember(User member){
        membersSet.add(member);
    }

    public void removeMembersIDs(User member) {
        membersSet.remove(member);
    }

    public void addMessage(Message msg){
        messageList.add(msg);
    }

    public void removeMessage(Message msg){
        messageList.remove(msg);
    }
}

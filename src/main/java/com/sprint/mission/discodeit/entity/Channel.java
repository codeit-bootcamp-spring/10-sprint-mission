package com.sprint.mission.discodeit.entity;

<<<<<<< HEAD
import java.io.Serializable;
import java.util.UUID;

public class Channel extends BaseEntity implements Serializable {
    private static final long serialVersion = 1L;

    private String name;
    private String description;

    public Channel(String name, String description) {
//        this.id = UUID.randomUUID();
//        this.createdAt = System.currentTimeMillis();
//        this.updatedAt = this.createdAt;

        this.name = name;
        this.description = description;
    }

    public void updateChannel (String name, String description) {
        this.name = name;
        this.description = description;
    }
    public void updateChannelName(String name) {
        this.name = name;
    }

    public void updateChannelDescription(String description) {
        this.description = description;
    }


    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }



}

=======
import java.util.ArrayList;
import java.util.List;

public class Channel extends BaseEntity{
    private String channelName;
    private List<User> participants = new ArrayList<>();
    private List<Message> channelMessages = new ArrayList<>();

    public String getChannelName() {
        return channelName;
    }

    public void updateChannelInfo(String newChannelName){
        this.channelName = newChannelName;
        super.setUpdatedAt(System.currentTimeMillis());
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void addParticipant(User user){
       this.participants.add(user);
    }

    public List<Message> getChannelMessages() {
        return channelMessages;
    }

    public void addMessage(Message message){
        this.channelMessages.add(message);
    }

    public Channel(String channelName) {
        this.channelName = channelName;
    }
}
>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f

package com.sprint.mission.entity;

import java.util.Map;
import java.util.UUID;

public class Channel extends Entity<Channel> {
    private String channelName;
    private final EntityManager<Message> messagesByID;
    private final EntityManager<User> usersByID;

    private Channel(Channel obj) {
        super(obj);
        this.channelName = obj.channelName;
        this.messagesByID = obj.messagesByID;
        this.usersByID = obj.usersByID;
    }

    public Channel(String channelName) {
        super();
        this.channelName = channelName;
        this.messagesByID = new EntityManager<>();
        this.usersByID = new EntityManager<>();
    }

    @Override
    public void update(String channelName) {
        this.channelName = channelName;
        updateTime();
    }

    @Override
    public Channel copy() {
        return new Channel(this);
    }

    public void addUser(User user) {
        this.usersByID.add(user);
    }

    public void addMessage(Message message) {
        this.messagesByID.add(message);
    }

    public void removeUser(UUID userId) {
        usersByID.remove(userId);
    }

    public void removeMessage(UUID messageId) {
        messagesByID.remove(messageId);
    }

    public String getChannelName() {
        return channelName;
    }

    public Message getMessage(UUID messageId) {
        return messagesByID.get(messageId);
    }

    public Map<UUID, Message> getMessagesByID(UUID messageId, UUID... messageIds) {
        return messagesByID.getEntitiesById(messageId, messageIds);
    }

    public User getUser(UUID userId) {
        return usersByID.get(userId);
    }

    public Map<UUID, User> getUsersByID(UUID userId, UUID... userIds) {
        return usersByID.getEntitiesById(userId, userIds);
    }

    public boolean hasMessage(UUID messageId) {
        return this.messagesByID.hasEntity(messageId);
    }

    public boolean hasUser(UUID userId) {
        return this.usersByID.hasEntity(userId);
    }

    @Override
    public String toString() {
        return String.format("channel name: %s\n%s", channelName, super.toString());
    }
}

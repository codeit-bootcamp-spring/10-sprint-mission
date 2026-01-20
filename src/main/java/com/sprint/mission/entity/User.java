package com.sprint.mission.entity;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class User extends Entity<User> {
    private String userName;
    private final EntityManager<Channel> channelsById;
    private final EntityManager<Message> messagesById;

    private User(User obj) {
        super(obj);
        this.userName = obj.userName;
        this.channelsById = obj.channelsById;
        this.messagesById = obj.messagesById;
    }

    public User(String userName) {
        super();
        this.userName = Objects.requireNonNull(userName);
        this.channelsById = new EntityManager<>();
        this.messagesById = new EntityManager<>();
    }

    @Override
    public User copy() {
        return new User(this);
    }

    @Override
    public void update(String userName) {
        this.userName = userName;
        updateTime();
    }

    public void addChannel(Channel channel) {
        this.channelsById.add(channel);
    }

    public void addMessage(Message message) {
        this.messagesById.add(message);
    }

    public void removeChannel(UUID channelId) {
        channelsById.remove(channelId);
    }

    public void removeMessage(UUID messageId) {
        messagesById.remove(messageId);
    }

    public String getUserName() {
        return userName;
    }

    public Channel getChannel(UUID channelId) {
        return channelsById.get(channelId);
    }

    public Map<UUID, Channel> getChannelsById(UUID channelId, UUID... channelIDs) {
        return channelsById.getEntitiesById(channelId, channelIDs);
    }

    public Message getMessage(UUID messageId) {
        return messagesById.get(messageId);
    }

    public Map<UUID, Message> getMessagesById(UUID messageID, UUID... messageIDs) {
        return messagesById.getEntitiesById(messageID, messageIDs);
    }

    public boolean hasChannel(UUID channelId) {
        return this.channelsById.hasEntity(channelId);
    }

    public boolean hasMessage(UUID messageId) {
        return this.messagesById.hasEntity(messageId);
    }

    @Override
    public String toString() {
        return String.format("user name: %s\n%s", userName, super.toString());
    }
}

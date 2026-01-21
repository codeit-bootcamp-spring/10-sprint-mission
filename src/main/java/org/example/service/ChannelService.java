package org.example.service;

import org.example.entity.Channel;
import org.example.entity.ChannelType;
import org.example.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel create(String name, String description, ChannelType type, UUID ownerId);

    Channel findById(UUID channelId);

    List<Channel> findAll();

    Channel update(UUID channelId, String name, String description, ChannelType type);

    void delete(UUID channelId);

    void addMember(UUID channelId, UUID userId);

    void removeMember(UUID channelId, UUID userId);

    void transferOwnership(UUID channelId, UUID newOwnerId);

    List<User> findMembersByChannel(UUID channelId);
}
package org.example.service;

import org.example.entity.Channel;
import org.example.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel create(String name, String description, ChannelType type, UUID ownerId);

    Channel findById(UUID id);

    List<Channel> findAll();

    Channel update(UUID id, String name, String description, ChannelType type);

    void transferOwnership(UUID channelId, UUID newOwnerId);

    void delete(UUID id);

    void addMember(UUID channelId, UUID userId);

    void removeMember(UUID channelId, UUID userId);
}

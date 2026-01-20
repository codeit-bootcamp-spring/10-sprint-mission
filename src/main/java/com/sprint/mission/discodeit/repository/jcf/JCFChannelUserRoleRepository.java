package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ChannelUserRole;
import com.sprint.mission.discodeit.repository.ChannelUserRoleRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFChannelUserRoleRepository implements ChannelUserRoleRepository {
    private final Map<UUID, ChannelUserRole> channelUserRoleDB = new HashMap<>();

    @Override
    public ChannelUserRole save(ChannelUserRole role) {
        channelUserRoleDB.put(role.getId(), role);
        return role;
    }

    @Override
    public Optional<ChannelUserRole> findById(UUID id) {
        return Optional.ofNullable(channelUserRoleDB.get(id));
    }

    @Override
    public Optional<ChannelUserRole> findByChannelIdAndUserId(UUID channelId, UUID userId) {
        return channelUserRoleDB.values().stream()
                .filter(cu -> cu.getChannel().getId().equals(channelId)
                        && cu.getUser().getId().equals(userId))
                .findFirst();
    }

    @Override
    public List<ChannelUserRole> findAllByChannelId(UUID channelId) {
        return channelUserRoleDB.values().stream()
                .filter(cu -> cu.getChannel().getId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChannelUserRole> findAllByUserId(UUID userId) {
        return channelUserRoleDB.values().stream()
                .filter(cu -> cu.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        channelUserRoleDB.remove(id);
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        channelUserRoleDB.values().removeIf(role -> role.getUser().getId().equals(userId));
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        channelUserRoleDB.values().removeIf(role -> role.getChannel().getId().equals(channelId));
    }

    @Override
    public boolean existsByChannelIdAndUserId(UUID channelId, UUID userId) {
        return channelUserRoleDB.values().stream()
                .anyMatch(cu -> cu.getChannel().getId().equals(channelId)
                        && cu.getUser().getId().equals(userId));
    }
}
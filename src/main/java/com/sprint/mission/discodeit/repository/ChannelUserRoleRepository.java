package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ChannelUserRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelUserRoleRepository {
    ChannelUserRole save(ChannelUserRole role);
    Optional<ChannelUserRole> findById(UUID id);
    Optional<ChannelUserRole> findByChannelIdAndUserId(UUID channelId, UUID userId);
    List<ChannelUserRole> findAllByChannelId(UUID channelId);
    List<ChannelUserRole> findAllByUserId(UUID userId);
    void delete(UUID id);
    void deleteAllByUserId(UUID userId);
    void deleteAllByChannelId(UUID channelId);
    boolean existsByChannelIdAndUserId(UUID channelId, UUID userId);
}
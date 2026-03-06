package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {
    boolean existsByChannelAndUser(Channel channel, User user);

    List<ReadStatus> findAllByUserId(UUID userId);

    List<ReadStatus> findAllByChannel(Channel channel);

    void deleteAllByChannelId(UUID channelId);
}

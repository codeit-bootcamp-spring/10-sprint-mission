package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;

public interface ChannelRepository {
    void save(Channel channel);
    void delete(UUID id);
    Optional<Channel> findById(UUID id);
    List<Channel> findAll();

}

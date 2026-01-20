package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.*;

public interface ChannelRepository {
    void save(Channel channel);
    void delete(UUID id);
    Channel findById(UUID id);
    List<Channel> findAll();
}

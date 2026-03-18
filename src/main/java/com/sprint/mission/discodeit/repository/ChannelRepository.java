package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  default UUID createChannel(Channel channel) {
    return save(channel).getId();
  }

  default Channel saveChannel(Channel channel) {
    return save(channel);
  }

  default Channel findChannel(UUID channelId) {
    return findById(channelId).orElse(null);
  }

  default List<Channel> findAllChannel() {
    return findAll();
  }

  default void deleteChannel(UUID channelId) {
    deleteById(channelId);
  }
}
package com.sprint.mission.discodeit.channel.repository;

import com.sprint.mission.discodeit.channel.entity.Channel;

import com.sprint.mission.discodeit.channel.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JPAChannelRepository extends JpaRepository<Channel, UUID> {

  List<Channel> findByTypeOrIdIn(ChannelType type, List<UUID> ids);


}

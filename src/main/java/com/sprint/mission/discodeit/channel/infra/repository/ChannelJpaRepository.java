package com.sprint.mission.discodeit.channel.infra.repository;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.channel.domain.repository.ChannelRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelJpaRepository extends JpaRepository<Channel, UUID>, ChannelRepository {

}

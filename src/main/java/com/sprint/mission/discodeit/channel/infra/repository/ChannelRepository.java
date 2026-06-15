package com.sprint.mission.discodeit.channel.infra.repository;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.channel.infra.repository.qdsl.ChannelQDSLRepository;
import com.sprint.mission.discodeit.common.jpa.repository.DomainRepository;

public interface ChannelRepository extends DomainRepository<Channel>, ChannelQDSLRepository {

}

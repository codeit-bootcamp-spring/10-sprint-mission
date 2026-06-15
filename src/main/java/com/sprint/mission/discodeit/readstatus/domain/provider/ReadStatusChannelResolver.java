package com.sprint.mission.discodeit.readstatus.domain.provider;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.common.reference.resolver.EntityReferenceResolver;
import java.util.UUID;

public interface ReadStatusChannelResolver extends EntityReferenceResolver<Channel> {

  Channel getProxy(UUID id);
}

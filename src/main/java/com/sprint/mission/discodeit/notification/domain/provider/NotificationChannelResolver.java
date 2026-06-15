package com.sprint.mission.discodeit.notification.domain.provider;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.common.reference.resolver.EntityReferenceResolver;
import java.util.UUID;

public interface NotificationChannelResolver extends EntityReferenceResolver<Channel> {

  String getChannelName(UUID id);
}

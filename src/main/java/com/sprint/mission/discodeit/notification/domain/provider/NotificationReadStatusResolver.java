package com.sprint.mission.discodeit.notification.domain.provider;

import com.sprint.mission.discodeit.common.reference.resolver.EntityReferenceResolver;
import com.sprint.mission.discodeit.readstatus.domain.entity.ReadStatus;
import java.util.List;
import java.util.UUID;

public interface NotificationReadStatusResolver extends EntityReferenceResolver<ReadStatus> {

  List<UUID> findUserIdsByChannelIdAndNotificationEnabledIsTrue(UUID channelId);
}

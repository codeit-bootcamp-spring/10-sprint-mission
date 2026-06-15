package com.sprint.mission.discodeit.notification.domain.provider;

import com.sprint.mission.discodeit.common.reference.resolver.EntityReferenceResolver;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.UUID;

public interface NotificationUserResolver extends EntityReferenceResolver<User> {

  String getUsername(UUID id);

  User getProxyByUsername(String username);
}

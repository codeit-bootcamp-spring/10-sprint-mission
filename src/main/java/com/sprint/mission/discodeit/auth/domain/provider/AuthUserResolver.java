package com.sprint.mission.discodeit.auth.domain.provider;

import com.sprint.mission.discodeit.common.reference.resolver.EntityReferenceResolver;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.UUID;

public interface AuthUserResolver extends EntityReferenceResolver<User> {

  User getProxy(UUID id);

  User getOrThrow(UUID id);
}

package com.sprint.mission.discodeit.readstatus.domain.provider;

import com.sprint.mission.discodeit.common.reference.resolver.EntityReferenceResolver;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.List;
import java.util.UUID;

public interface ReadStatusUserResolver extends EntityReferenceResolver<User> {

  List<User> getProxy(List<UUID> ids);
}

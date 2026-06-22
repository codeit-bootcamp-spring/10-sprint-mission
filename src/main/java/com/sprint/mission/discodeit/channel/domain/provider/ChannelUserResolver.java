package com.sprint.mission.discodeit.channel.domain.provider;

import com.sprint.mission.discodeit.common.reference.resolver.EntityReferenceResolver;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ChannelUserResolver extends EntityReferenceResolver<User> {

  List<User> getOrThrow(Collection<UUID> ids);
}

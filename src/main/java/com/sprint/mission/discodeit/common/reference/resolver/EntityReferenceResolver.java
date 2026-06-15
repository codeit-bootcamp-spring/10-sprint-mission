package com.sprint.mission.discodeit.common.reference.resolver;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface EntityReferenceResolver<T> {

  T getProxyOrThrow(UUID id);

  List<T> getProxyOrThrow(Collection<UUID> ids);
}

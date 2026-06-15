package com.sprint.mission.discodeit.common.reference.supplier;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface EntityReferenceSupplier<T> {

  void existsOrThrow(UUID id);

  void existsOrThrow(Collection<UUID> ids);

  T getProxy(UUID id);

  List<T> getProxy(Collection<UUID> ids);

  T getOrThrow(UUID id);

  List<T> getOrThrow(Collection<UUID> ids);
}

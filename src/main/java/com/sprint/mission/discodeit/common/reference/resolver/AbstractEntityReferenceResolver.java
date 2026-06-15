package com.sprint.mission.discodeit.common.reference.resolver;

import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractEntityReferenceResolver<T> implements EntityReferenceResolver<T> {

  protected final EntityReferenceSupplier<T> service;

  @Override
  public List<T> getProxyOrThrow(Collection<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }
    service.existsOrThrow(ids);
    return service.getProxy(ids);
  }

  @Override
  public T getProxyOrThrow(UUID id) {
    service.existsOrThrow(id);
    return service.getProxy(id);
  }
}

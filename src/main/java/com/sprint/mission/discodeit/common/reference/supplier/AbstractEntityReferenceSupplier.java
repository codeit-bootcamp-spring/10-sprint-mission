package com.sprint.mission.discodeit.common.reference.supplier;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.jpa.repository.EntityReferenceJpaRepository;
import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractEntityReferenceSupplier<T> implements EntityReferenceSupplier<T> {

  private final EntityReferenceJpaRepository<T> repository;

  protected abstract DiscodeitException notFoundException(UUID id);

  protected abstract DiscodeitException notFoundException(Collection<UUID> ids);

  @Override
  public void existsOrThrow(UUID id) {
    DomainServiceSupport.requireOrThrow(id, repository::existsById, this::notFoundException);
  }

  @Override
  public void existsOrThrow(Collection<UUID> ids) {
    List<UUID> existingIds = repository.filterExistingIds(ids);
    if (existingIds.size() != ids.size()) {
      throw notFoundException(ids);
    }
  }

  @Override
  public T getProxy(UUID id) {
    return repository.getReferenceById(id);
  }

  @Override
  public List<T> getProxy(Collection<UUID> ids) {
    return ids.stream().map(repository::getReferenceById).toList();
  }

  @Override
  public T getOrThrow(UUID id) {
    return DomainServiceSupport.getOrThrow(id, repository::findById, this::notFoundException);
  }

  @Override
  public List<T> getOrThrow(Collection<UUID> ids) {
    existsOrThrow(ids);
    return repository.findAllById(ids);
  }
}

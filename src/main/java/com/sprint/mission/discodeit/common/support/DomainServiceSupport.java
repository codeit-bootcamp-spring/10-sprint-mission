package com.sprint.mission.discodeit.common.support;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.common.CommonErrorCode;
import com.sprint.mission.discodeit.common.exception.common.CommonException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;


@Component
public class DomainServiceSupport {

  public <T, R> R getOrThrow(T value, Function<T, Optional<R>> action,
      Function<T, DiscodeitException> exception) {
    return action.apply(value).orElseThrow(() -> exception.apply(value));
  }

  public <T> void deleteByIdOrThrow(UUID id, JpaRepository<T, UUID> repository,
      Function<UUID, DiscodeitException> exception) {
    T entity = repository.findById(id).orElseThrow(() -> exception.apply(id));
    try {
      repository.delete(entity);
    } catch (OptimisticLockingFailureException e) {
      // todo: new UserException(CommonErrorCode...), ChannelException(CommonErrorCode...), ...
      throw new CommonException(CommonErrorCode.OPTIMISTIC_LOCKING_FAILURE);
    }
  }

  public <T> void throwOrNot(T value, Predicate<T> condition,
      Function<T, DiscodeitException> exception) {
    if (!condition.test(value)) {
      throw exception.apply(value);
    }
  }
}

package com.sprint.mission.discodeit.common.support;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DomainServiceSupport {

  public static <T, R> R getOrThrow(T value,
      Function<T, Optional<R>> action,
      Function<T, DiscodeitException> exception) {
    return action.apply(value).orElseThrow(() -> exception.apply(value));
  }

  public static <T> void deleteOrThrow(UUID id,
      JpaRepository<T, UUID> repository,
      Function<UUID, DiscodeitException> exception) {
    T entity = repository.findById(id).orElseThrow(() -> exception.apply(id));
    repository.delete(entity);
  }

  public static <T> void requireOrThrow(T value,
      Predicate<T> condition,
      Function<T, DiscodeitException> exception) {
    if (!condition.test(value)) {
      throw exception.apply(value);
    }
  }
}

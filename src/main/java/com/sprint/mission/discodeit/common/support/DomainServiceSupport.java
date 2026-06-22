package com.sprint.mission.discodeit.common.support;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DomainServiceSupport {

  public static <T, R> R getOrThrow(T value,
      Function<T, Optional<R>> action,
      Function<T, DiscodeitException> exception) {
    return action.apply(value).orElseThrow(() -> exception.apply(value));
  }

  public static <T> void requireOrThrow(T value,
      Predicate<T> condition,
      Function<T, DiscodeitException> exception) {
    if (!condition.test(value)) {
      throw exception.apply(value);
    }
  }
}

package com.sprint.mission.discodeit.common.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class BaseUpdatableEntity extends BaseEntity {

  @Column
  @LastModifiedDate
  private Instant updatedAt;

  protected <T> boolean shouldUpdate(T oldValue, T newValue, Consumer<T> validator) {
    if (newValue == null || Objects.equals(oldValue, newValue)) {
      return false;
    }
    validator.accept(newValue);
    return true;
  }

  protected <T> void updateIfChanged(T oldValue, T newValue, Consumer<T> action) {
    if (newValue == null || Objects.equals(oldValue, newValue)) {
      return;
    }
    action.accept(newValue);
  }
}

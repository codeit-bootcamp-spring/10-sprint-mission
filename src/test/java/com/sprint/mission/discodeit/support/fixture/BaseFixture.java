package com.sprint.mission.discodeit.support.fixture;

import static org.instancio.Select.field;

import com.sprint.mission.discodeit.common.entity.BaseEntity;
import com.sprint.mission.discodeit.common.entity.BaseUpdatableEntity;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.InstancioApi;

public enum BaseFixture {
  INSTANT;

  public <T extends BaseEntity> InstancioApi<T> baseEntity(Class<T> type) {
    return Instancio.of(type).ignore(field(BaseEntity::getCreatedAt));
  }

  public <T extends BaseEntity> InstancioApi<List<T>> baseEntities(Class<T> type) {
    return Instancio.ofList(type).ignore(field(BaseEntity::getCreatedAt));
  }

  public <T extends BaseUpdatableEntity> InstancioApi<T> baseUpdatableEntity(Class<T> type) {
    return Instancio.of(type)
        .ignore(field(BaseUpdatableEntity::getCreatedAt))
        .ignore(field(BaseUpdatableEntity::getUpdatedAt));
  }

  public <T extends BaseUpdatableEntity> InstancioApi<List<T>> baseUpdatableEntities(
      Class<T> type) {
    return Instancio.ofList(type)
        .ignore(field(BaseUpdatableEntity::getCreatedAt))
        .ignore(field(BaseUpdatableEntity::getUpdatedAt));
  }
}

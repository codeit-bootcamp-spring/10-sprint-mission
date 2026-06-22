package com.sprint.mission.discodeit.common.mapper.util;

import com.sprint.mission.discodeit.common.jpa.BaseEntity;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.Named;

@Mapper(componentModel = ComponentModel.SPRING)
public interface UtilMapper {

  @Named("extractIds")
  default <T extends BaseEntity> List<UUID> extractIds(Collection<T> entities) {
    if (entities == null || entities.isEmpty()) {
      return Collections.emptyList();
    }
    return entities.stream().map(BaseEntity::getId).toList();
  }
}

package com.sprint.mission.discodeit.common.mapper;

import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.NullValueMappingStrategy;

public interface BaseMapper<T, R> {

  R toDto(T entity);

  @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
  List<R> toDto(List<T> entities);
}

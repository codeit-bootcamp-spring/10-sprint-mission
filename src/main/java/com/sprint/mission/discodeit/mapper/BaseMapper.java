package com.sprint.mission.discodeit.mapper;

import java.util.List;

public interface BaseMapper<T, R> {
  R toDto(T entity);
  List<R> toDto(List<T> entities);
}

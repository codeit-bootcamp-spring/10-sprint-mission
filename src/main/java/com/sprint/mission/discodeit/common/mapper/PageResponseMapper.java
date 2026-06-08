package com.sprint.mission.discodeit.common.mapper;

import com.sprint.mission.discodeit.common.dto.response.PageResponse;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Slice;

public interface PageResponseMapper<T, R> {

  @Mapping(target = "content", defaultExpression = "java(java.util.List.of())")
  PageResponse<R> fromSlice(Slice<T> slice);
}

package com.sprint.mission.discodeit.common.mapper;

import com.sprint.mission.discodeit.common.api.response.PageResponse;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Slice;

public interface PageResponseMapper<T, R> {

  @Mapping(target = "content", source = "content", defaultExpression = "java(java.util.Collections.emptyList())")
  PageResponse<R> fromSlice(Slice<T> slice);
}

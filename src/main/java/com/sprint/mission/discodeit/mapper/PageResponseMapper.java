package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

public interface PageResponseMapper<T, R> {

  @Mapping(target = "content", defaultExpression = "java(java.util.List.of())")
  PageResponse<R> fromSlice(Slice<T> slice);

  PageResponse<R> fromPage(Page<T> page);
}

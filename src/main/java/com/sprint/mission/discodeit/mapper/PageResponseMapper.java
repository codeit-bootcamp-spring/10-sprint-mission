package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.page.PageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.function.Function;

@Mapper(componentModel = "spring")
public interface PageResponseMapper {
    // Slice로부터 DTO 생성 (전체 개수 모름 -> null)
    default  <T> PageResponse<T> fromSlice(Slice<T> slice, Object nextCursor) {
        return new PageResponse<>(
                slice.getContent(),
                nextCursor,
                slice.getSize(),
                slice.hasNext(),
                null
        );
    }

    // Page로부터 DTO 생성 (전체 개수 포함)
    default  <T> PageResponse<T> fromPage(Page<T> page, Object nextCursor) {
        return new PageResponse<>(
                page.getContent(),
                nextCursor,
                page.getSize(),
                page.hasNext(),
                page.getTotalElements()
        );
    }
}

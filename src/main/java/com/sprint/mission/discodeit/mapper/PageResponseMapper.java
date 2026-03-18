package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PageResponseMapper {
    default <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.hasNext(),
                page.getTotalElements()
        );
    }

    default <T> PageResponse<T> fromSlice(Slice<T> slice) {
        return new PageResponse<>(
                slice.getContent(),
                null,
                slice.getSize(),
                slice.hasNext(),
                null
        );
    }

    default <T> PageResponse<T> fromData(List<T> data, Instant nextCursor, boolean hasNext) {
        return new PageResponse<>(
                data,
                nextCursor,
                data.size(),
                hasNext,
                null
        );
    }
}

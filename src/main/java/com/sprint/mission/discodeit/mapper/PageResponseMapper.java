package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
public class PageResponseMapper {

    public <T> PageResponse<T> fromCursor(
            java.util.List<T> content,
            Object nextCursor,
            int size,
            boolean hasNext
    ) {
        return new PageResponse<>(
                content,
                nextCursor,
                size,
                hasNext,
                null
        );
    }

    public <T> PageResponse<T> fromSlice(Slice<T> slice) {
        return new PageResponse<>(
                slice.getContent(),
                null,
                slice.getSize(),
                slice.hasNext(),
                null
        );
    }

    public <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                null,
                page.getSize(),
                page.hasNext(),
                page.getTotalElements()
        );
    }
}
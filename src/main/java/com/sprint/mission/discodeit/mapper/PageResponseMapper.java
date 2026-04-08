package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PageResponseMapper {

    public <T> PageResponse<T> fromSlice(Slice<?> slice, List<T> dtos, Object nextCursor) {
        return new PageResponse<T>(
                dtos,
                nextCursor,     // cursor값 반영
                slice.getSize(),
                slice.hasNext(),
                null        // slice에선 totalElements가 없어도 됨
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

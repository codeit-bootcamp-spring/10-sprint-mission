package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

@Mapper(componentModel = "spring")
public class PageResponseMapper {

    public <T> PageResponse<T> fromSlice(Slice<T> slice, Instant cursor) {
        return new PageResponse<T>(
            slice.getContent(),
            cursor,
            slice.getSize(),
            slice.hasNext(),
            null
        );
    }

    public <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<T>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.hasNext(),
            page.getTotalElements()
        );
    }
}

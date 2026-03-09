package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PageResponseMapper {

    public <T> PageResponse<T> fromSlice(Slice<?> slice, List<T> dtos) {
        return new PageResponse<T>(
                dtos,
                slice.getNumber() + 1,      // 응답 방식 보정: page값과 맞추기 위함
                slice.getSize(),
                slice.hasNext(),
                null        // slice에선 totalElements가 없어도 됨
        );
    }

    public <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber() + 1,       // 응답 방식 보정: page값과 맞추기 위함
                page.getSize(),
                page.hasNext(),
                page.getTotalElements()
        );
    }
}

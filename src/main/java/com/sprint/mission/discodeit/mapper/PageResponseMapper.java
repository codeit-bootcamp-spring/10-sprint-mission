package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

@Mapper(componentModel = "spring")
public interface PageResponseMapper {

    default <T> PageResponse<T> fromSlice(Slice<T> slice) {
        return new PageResponse<>(
                slice.getContent(),
                slice.getNumber(),
                slice.getSize(),
                slice.hasNext(),
                null// Slice는 전체 개수 조회 안함(Count 쿼리 실행 안함) 즉 알 수 없음
        );
    }

    default <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.hasNext(),
                page.getTotalElements()
        );
    }
}

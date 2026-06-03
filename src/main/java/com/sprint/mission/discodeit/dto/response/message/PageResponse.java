package com.sprint.mission.discodeit.dto.response.message;

import lombok.Builder;

import java.util.List;

@Builder
public record PageResponse<T> (
    List<T> content,                // 실제 데이터
    Object nextCursor,              // 커서
    int size,                       // 페이지 크기 (50 개)
    boolean hasNext,                // 다음 페이지 존재 여부
    Long totalElements              // 총 개수 (null 허용)
) {

}

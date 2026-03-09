package com.sprint.mission.discodeit.dto.response;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        // 페이지 번호
        int number,
        // 페이지 크기
        int size,
        // 다음 페이지 존재
        boolean hasNext,
        // 전체 개수
        Long totalElements
) {
}

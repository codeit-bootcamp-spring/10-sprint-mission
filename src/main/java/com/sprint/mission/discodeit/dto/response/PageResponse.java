package com.sprint.mission.discodeit.dto.response;

import java.util.List;

public record PageResponse<T>(
    List<T> content, // 실제 조회된 데이터 목록
    Object nextCursor, // 다음 페이지의 시작점
    int size, // 페이지 당 데이터 수
    boolean hasNext, // 다음 페이지 존재 여부
    Long totalElements // 전체 데이터 총 개수
) {

}

package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.message.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PageResponseMapper {
    public <T> PageResponse<T> fromCursor(List<T> content, String nextCursor, int size, boolean hasNext, Long totalElements) {
        return PageResponse.<T>builder()
                .content(content)
                .nextCursor(nextCursor)
                .size(size)
                .hasNext(hasNext)
                .totalElements(totalElements)
                .build();
    }
}

package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.hibernate.grammars.hql.HqlParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.List;

public class PageResponseMapper {
    private PageResponseMapper() {}
    public static <T> PageResponse<T> fromCursorPage(
            List<T> content,
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
}

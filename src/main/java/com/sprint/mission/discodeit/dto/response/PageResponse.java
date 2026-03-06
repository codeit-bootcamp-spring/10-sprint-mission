package com.sprint.mission.discodeit.dto.response;

import java.util.List;

public class PageResponse<T> {

    private List<T> content;
    private int number;
    private int size;
    private boolean hasNext;
    private long totalElements;
}

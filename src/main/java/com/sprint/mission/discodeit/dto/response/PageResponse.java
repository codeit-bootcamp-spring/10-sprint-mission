package com.sprint.mission.discodeit.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    public List<T> content; // 실제 데이터
    public Object nextCursor;
    public int size; // 페이지 크기
    public boolean hasNext;
    public Long totalElements; // T 데이터의 총 갯수. null 일 수 있다.


}

package com.sprint.mission.discodeit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@Getter
@AllArgsConstructor
public class PageResponse<T>{

    List<T> content; //실제 데이터
    int number; //페이지 번호
    int size; //페이지의 크기
    boolean hasNext;
    Long totalElements; //T데이터의 총갯수 null가능

}

package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

// 바이너리 데이터 표현
@Getter
public class BinaryContent extends Base {
    private final byte[] data;
    private final String contentType;

    public BinaryContent(byte[] data, String contentType){
        super();
        this.data = data;
        this.contentType = contentType;
    }
}

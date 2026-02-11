package com.sprint.mission.discodeit.entity;

import lombok.Getter;

/*
* 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델
* 수정 불가능한 도메인 모델
* */
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

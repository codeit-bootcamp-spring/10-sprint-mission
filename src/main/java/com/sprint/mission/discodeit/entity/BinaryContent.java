package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;
    /* - 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델입니다.
    사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용합니다.
     */
    private UUID id;
    private Instant createdAt;
    //수정 불가
    private String fileName;
    private byte[] bytes;

    public BinaryContent(String fileName, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.bytes = bytes;
    }

}

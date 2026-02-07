package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

// 사용자의 프로필 이미지, 메시지에 첨부된 파일 저장하기 위해 활용하는
// -> User가 참조할 수 있게 profileId 추가해야함
// -> Message에 어떤 첨부파일들이 있는 지 참조할 수 있게 attatchmentIds라는 List를 추가해야함
// 수정 불가능한 도메인(updatedAt 필드 정의 X)
@Getter
public class BinaryContent implements Serializable {
    private UUID id;
    private Instant createdAt;
    private byte[] content;
    private String contentType;

    public BinaryContent(String contentType, byte[] content) {
        this.id = UUID.randomUUID();
        this.contentType = contentType;
        this.content = content;
        this.createdAt = Instant.now();
    }
}

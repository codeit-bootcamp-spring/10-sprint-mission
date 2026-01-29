package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDTO;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {                             // 수정 불가능 클래스
    private UUID id;
    private Instant createdTime;                         // 파일 생성 시점
    private String fileName;                             // 파일 이름
    private byte[] binaryContent;                        // 실제 파일
    private BinaryContentType binaryContentType;         // 파일 종류

    public BinaryContent(BinaryContentCreateRequestDTO binaryContentCreateRequestDTO){
        this.id = UUID.randomUUID();
        this.createdTime = Instant.now();
        this.fileName = binaryContentCreateRequestDTO.getFileName();
        this.binaryContent = binaryContentCreateRequestDTO.getBinaryContent();
        this.binaryContentType = binaryContentCreateRequestDTO.getBinaryContentType();
    }
}
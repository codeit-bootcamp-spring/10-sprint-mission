package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import org.springframework.util.Assert;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private String fileName;
    private String contentType;
    private long size;

    private byte[] contents;

    public BinaryContent(String fileName, String contentType, long size, byte[] contents) {
        Assert.hasText(fileName, "파일 이름이 없습니다!");
        Assert.isTrue(size > 0, "파일의 크기가 유효하지 않습니다!");
        Assert.notNull(contents, "첨부할 파일이 없습니다!");
        Assert.isTrue(contents.length > 0, "첨부할 파일이 비어있습니다!");

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.contents = contents;
    }
}

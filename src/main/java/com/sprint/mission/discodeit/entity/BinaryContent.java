package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

@Getter
public class BinaryContent{
    private final UUID id;
    private String fileName;
    private String fileType;
    private byte[] bytes;

    public BinaryContent(String fileName, String fileType) {
        this.id = UUID.randomUUID();
        this.fileName = fileName;
        this.fileType = fileType;
    }
}

package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable{
    private final UUID id;
    private final Instant createdAt;
    private final String contentType;
    private final byte[] file;

    public BinaryContent(String contentType, byte[] file){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.contentType = contentType;
        this.file = file;
    }

}

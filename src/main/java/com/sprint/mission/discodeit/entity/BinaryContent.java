package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent extends BaseEntity {

    private final String contentType;
    private final byte[] file;

    public BinaryContent(String contentType, byte[] file) {
        this.contentType = contentType;
        this.file = file;
    }

}

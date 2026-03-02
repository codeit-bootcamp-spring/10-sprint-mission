package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sprint.mission.discodeit.response.ApiException;
import com.sprint.mission.discodeit.response.ErrorCode;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@JsonPropertyOrder({"id", "createdAt", "fileName", "size", "contentType", "bytes"})
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    @JsonIgnore
    private final UUID ownerId;
    @JsonIgnore
    private final BinaryContentOwnerType binaryContentOwnerType;
    private final byte[] bytes;
    private final String contentType;
    private final String fileName;
    private final Instant createdAt;

    public BinaryContent(
            UUID ownerId,
            BinaryContentOwnerType binaryContentOwnerType,
            byte[] bytes,
            String contentType,
            String fileName
    ) {
        validateContentType(contentType);

        this.contentType = contentType;
        this.fileName = fileName;
        this.id = UUID.randomUUID();
        this.ownerId = ownerId;
        this.binaryContentOwnerType = binaryContentOwnerType;
        this.bytes = bytes;

        this.createdAt = Instant.now();
    }

    private void validateContentType(String contentType) {
        if (!contentType.startsWith("image/")) {
            throw new ApiException(
                    ErrorCode.INVALID_CONTENT_TYPE,
                    "이미지만 업로드 가능합니다"
            );
        }
    }

    public long getSize() {
        return bytes.length;
    }
}

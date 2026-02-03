package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import java.util.List;
import java.util.UUID;

@Getter
public class MessageUpdateRequest {
    private final UUID id;
    private final String content;
    private final List<BinaryContentRequest> binaryContents;

    public MessageUpdateRequest(UUID id, String content, List<BinaryContentRequest> binaryContents) {
        if (id == null) throw new IllegalArgumentException("ID는 필수입니다.");
        this.id = id;
        this.content = content;
        this.binaryContents = binaryContents;
    }

    public MessageUpdateRequest(UUID id, String content) {
        this(id, content, null);
    }
}
package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ChannelUpdateRequest {
    private UUID id;
    private String name;
    private String type;
    private String description;

    public ChannelUpdateRequest(UUID id, String name, String type, String description) {
        validate(id, type);
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
    }
    public ChannelUpdateRequest(UUID id, String name, String type) {
        this(id, name, type, null);
    }

    private void validate(UUID id, String type) {
        if (id == null) {
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }
        if (type != null && !type.equals("PUBLIC") && !type.equals("PRIVATE")) {
            throw new IllegalArgumentException("채널 타입은 PUBLIC 또는 PRIVATE이어야 합니다.");
        }
    }
}
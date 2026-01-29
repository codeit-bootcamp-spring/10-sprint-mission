package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class ChannelCreateRequest {
    private final String name;
    private final String type;
    private final String description;
    private final List<UUID> participantIds;

    public ChannelCreateRequest(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("PUBLIC 채널은 이름이 필수입니다.");
        }
        this.name = name;
        this.type = "PUBLIC";
        this.description = description;
        this.participantIds = Collections.emptyList();
    }

    public ChannelCreateRequest(List<UUID> participantIds) {
        if (participantIds == null || participantIds.isEmpty()) {
            throw new IllegalArgumentException("PRIVATE 채널은 최소 1명 이상의 참여자가 필요합니다.");
        }
        this.name = null;
        this.type = "PRIVATE";
        this.description = null;
        this.participantIds = participantIds;
    }
}
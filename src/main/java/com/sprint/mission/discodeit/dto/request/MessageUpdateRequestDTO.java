package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MessageUpdateRequestDTO {
    private UUID id;
    private String message;
}

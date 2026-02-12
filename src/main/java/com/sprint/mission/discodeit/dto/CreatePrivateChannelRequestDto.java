package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreatePrivateChannelRequestDto {
    private String name;
    private List<UUID> participantIds;
}

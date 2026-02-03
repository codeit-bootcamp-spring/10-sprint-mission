package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class BinaryContentResponseDto {
    private final UUID id;
    private final byte[] bytes;
    private final String fileName;
    private final String contentType;
}

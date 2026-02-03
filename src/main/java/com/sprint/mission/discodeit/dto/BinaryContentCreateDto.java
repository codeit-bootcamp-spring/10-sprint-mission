package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BinaryContentCreateDto {
    private final byte[] bytes;
    private final String filename;
    private final String contentType;
}

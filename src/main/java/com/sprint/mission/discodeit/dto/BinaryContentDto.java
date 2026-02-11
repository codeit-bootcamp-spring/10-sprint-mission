package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BinaryContentDto {
    private final byte[] bytes;
    private final String fileName;
    private final String contentType;
}

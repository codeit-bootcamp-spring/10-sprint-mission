package com.sprint.mission.discodeit.dto.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;


@Getter
@AllArgsConstructor
public class BinaryContentDto {
        UUID id;
        String fileName;
        Long size;
        String contentType;
        byte[] bytes;
}

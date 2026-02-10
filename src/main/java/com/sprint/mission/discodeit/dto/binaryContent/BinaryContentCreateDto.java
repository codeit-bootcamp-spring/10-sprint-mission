package com.sprint.mission.discodeit.dto.binaryContent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Setter
public class BinaryContentCreateDto {
    private String name;
    private String contentType;
    private byte[] fileData;
}

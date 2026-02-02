package com.sprint.mission.discodeit.dto.binaryContent;

import com.sprint.mission.discodeit.entity.type.FileType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class BinaryContentCreateDto {
    private String name;
    private FileType fileType;
    private byte[] fileData;
}

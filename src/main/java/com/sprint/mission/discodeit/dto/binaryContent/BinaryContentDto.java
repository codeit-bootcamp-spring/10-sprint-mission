package com.sprint.mission.discodeit.dto.binaryContent;

import com.sprint.mission.discodeit.entity.FileType;
import lombok.Getter;

@Getter
public class BinaryContentDto {
    private String name;
    private FileType fileType;
    private byte[] fileData;
}

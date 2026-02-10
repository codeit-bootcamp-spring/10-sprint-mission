package com.sprint.mission.discodeit.dto.binaryContent;

import com.sprint.mission.discodeit.entity.type.FileType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Setter
public class BinaryContentCreateDto {
    private String name;
    private FileType fileType;
    private byte[] fileData;
}

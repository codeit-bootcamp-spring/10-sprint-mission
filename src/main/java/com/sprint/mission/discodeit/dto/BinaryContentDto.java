package com.sprint.mission.discodeit.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BinaryContentDto {

    private UUID id;
    private String fileName;
    private long size;
    private String contentType;
}

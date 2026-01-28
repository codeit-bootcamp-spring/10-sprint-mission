package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.BinaryContentType;
import lombok.Getter;

@Getter
public class BinaryContentCreateRequestDTO {
    private byte[] binaryContent;
    private BinaryContentType binaryContentType;
}

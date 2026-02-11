package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContentOwnerType;
import org.springframework.web.multipart.MultipartFile;

public record BinaryContentRequest (
        BinaryContentOwnerType type,
        MultipartFile file
) {
}

package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record MessageCreateRequest(
        @NotBlank(message = "메시지를 입력해주세요.")
        String content,

        List<BinaryContentCreateRequest> binaryContentCreateRequests
) {
}

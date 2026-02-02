package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;

public record MessageRequestDto(
        String content,
        UUID channelId,
        UUID authorId,
        List<UUID>attachmentIds //BinaryContent id의 List(한 메시지당 여러 파일 첨부 가능)
) {}

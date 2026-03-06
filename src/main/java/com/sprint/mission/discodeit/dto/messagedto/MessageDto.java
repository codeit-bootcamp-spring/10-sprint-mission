package com.sprint.mission.discodeit.dto.messagedto;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String content,
    UUID channelId,
    UserDto author,
    List<BinaryContentDto> attachmentIds
) {

}

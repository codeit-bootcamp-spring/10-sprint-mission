package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.common.BinaryContentParam;

import java.util.List;
import java.util.UUID;

public record MessageRequestCreateDto(String content,
                                      UUID channelId,
                                      UUID authorId,
                                      List<BinaryContentParam> attachments) {
}

package com.sprint.mission.discodeit.dto.request;


import java.util.UUID;

public record MessageCreateRequestDTO(
   String content,
   UUID channelId,
   UUID authorId
) {}

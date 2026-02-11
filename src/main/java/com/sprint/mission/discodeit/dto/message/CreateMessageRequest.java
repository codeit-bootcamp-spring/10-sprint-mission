package com.sprint.mission.discodeit.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageRequest {
    private UUID channelId;
    private UUID authorId;
    private String content;
    private List<UUID> attachmentIds;
}

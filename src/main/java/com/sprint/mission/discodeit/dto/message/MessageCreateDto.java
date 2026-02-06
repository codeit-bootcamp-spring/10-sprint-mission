package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageCreateDto {
    private UUID userId;
    private UUID channelId;
    private String text;
    private List<BinaryContentCreateDto> binaryContentList;
}

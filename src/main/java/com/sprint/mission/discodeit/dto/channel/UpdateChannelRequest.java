package com.sprint.mission.discodeit.dto.channel;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChannelRequest {
    private UUID channelId;     // 수정할 채널 ID
    private String name;        // 수정할 이름
    private String description; // 수정할 설명
}

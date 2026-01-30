package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public class MessageDto {
    // 요청 DTO
    public record MessageRequest(
            String content,
            User user,
            Channel channel
    ){}
}

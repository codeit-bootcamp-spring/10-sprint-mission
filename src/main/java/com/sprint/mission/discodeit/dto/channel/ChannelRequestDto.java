package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.user.UserRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.ChannelType;

public record ChannelRequestDto(
//        ChannelType type,
        String name,
        String discription,
        ReadStatusRequestDto readStatusDto,
        UserResponseDto userDto
) {

}

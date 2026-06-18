package com.sprint.mission.discodeit.event.kafka;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.user.UserDto;

public record KafkaUserChangedEvent(
    UserDto userDto,
    String action
) {

}

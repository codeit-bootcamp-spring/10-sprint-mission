package com.sprint.mission.discodeit.event.kafka;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;

public record KafkaChannelChangedEvent(
    ChannelDto channelDto,
    String action
) {

}

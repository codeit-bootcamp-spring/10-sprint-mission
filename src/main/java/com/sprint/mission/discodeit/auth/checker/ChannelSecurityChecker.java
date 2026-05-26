package com.sprint.mission.discodeit.auth.checker;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component("channelChecker")
@RequiredArgsConstructor
public class ChannelSecurityChecker {

    private final ChannelRepository channelRepository;

    @Transactional(readOnly = true)
    public boolean isPublic(UUID channelId){
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));
        return channel.getType() == ChannelType.PUBLIC;
    }
}

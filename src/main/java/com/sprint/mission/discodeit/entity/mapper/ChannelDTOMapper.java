package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.channeldto.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.channeldto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ChannelDTOMapper {
    public ChannelResponseDTO channelToResponseDTO(Channel channel, Message latestMessage){
        return new ChannelResponseDTO(
                channel.getId(),
                latestMessage != null ? latestMessage.getCreatedAt() : null,
                latestMessage != null ? latestMessage.getUpdatedAt() : null,
                channel.getUserList());
    }

    public Channel privateReqToChannel(PrivateChannelCreateDTO req){
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channel.getUserList().addAll(req.users());
        return channel;
    }

    public Channel publicReqToChannel(PublicChannelCreateDTO req){
        return new Channel(ChannelType.PUBLIC, req.name(), req.description());
    }
}

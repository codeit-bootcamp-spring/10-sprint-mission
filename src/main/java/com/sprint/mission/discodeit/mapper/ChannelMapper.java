package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.response.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponsePrivateDto;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponsePublicDto;
import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.stereotype.Component;


@Component
public class ChannelMapper {
    public ChannelResponseDto toPublicDto(Channel channel){
        if(channel == null) return null;

        return new ChannelResponsePublicDto(channel.getId(),
                channel.getChannelName(),
                channel.getMessageList(),
                channel.getChannelType());
    }

    public ChannelResponseDto toPrivateDto(Channel channel){
        if(channel == null) return null;

        return new ChannelResponsePrivateDto(channel.getId(),
                channel.getChannelName(),
                channel.getUserList(),
                channel.getMessageList(),
                channel.getChannelType());
    }
}

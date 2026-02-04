package com.sprint.mission.discodeit.mapper.channel;

import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPrivate;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPublic;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.stereotype.Component;

@Component
public class ChannelMapper {
    // public 일때

    // DTO -> Entity
    public Channel toEntity(ChannelCreateRequestPublic channelCreateRequestPublic){
        return new Channel(channelCreateRequestPublic.name());
    }

    // Entity -> DTO
    public ChannelResponse toResponse(Channel channel){
        return new ChannelResponse(
                channel.getId(),
                channel.getName(),
                channel.getDescriptions()
        );}

    // private 일 때
    // DTO -> Entity
    public Channel toEntity(ChannelCreateRequestPrivate channelCreateRequestPrivate){
        return new Channel();
    }

    // Entity -> DTO
    public ChannelResponse toResponse(Channel channel, String descriptions){
        return new ChannelResponse(
                channel.getId(),
                channel.getName(),
                descriptions
        );}
}

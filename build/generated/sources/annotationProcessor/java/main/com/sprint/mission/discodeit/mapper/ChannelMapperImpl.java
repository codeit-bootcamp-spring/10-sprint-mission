package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-22T16:33:34+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.4.jar, environment: Java 17.0.17 (Azul Systems, Inc.)"
)
@Component
public class ChannelMapperImpl implements ChannelMapper {

    @Override
    public ChannelResponse toResponse(Channel channel, Instant lastMessageTime, List<UUID> participantIds) {
        if ( channel == null && lastMessageTime == null && participantIds == null ) {
            return null;
        }

        UUID channelId = null;
        String description = null;
        if ( channel != null ) {
            channelId = channel.getId();
            description = channel.getDescription();
        }
        Instant lastMessageTime1 = null;
        lastMessageTime1 = lastMessageTime;
        List<UUID> participantIds1 = null;
        List<UUID> list = participantIds;
        if ( list != null ) {
            participantIds1 = new ArrayList<UUID>( list );
        }

        String channelName = channel.getChannelName();
        boolean isPrivate = channel.isPrivate();

        ChannelResponse channelResponse = new ChannelResponse( channelId, channelName, description, isPrivate, lastMessageTime1, participantIds1 );

        return channelResponse;
    }
}

package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-19T18:26:07+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.4.jar, environment: Java 17.0.17 (Azul Systems, Inc.)"
)
@Component
public class MessageDtoMapperImpl implements MessageDtoMapper {

    @Override
    public MessageDto toDto(Message message, UserDto author, List<BinaryContentDto> attachments) {
        if ( message == null && author == null && attachments == null ) {
            return null;
        }

        UUID id = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        String content = null;
        UUID channelId = null;
        if ( message != null ) {
            id = message.getId();
            createdAt = message.getCreatedAt();
            updatedAt = message.getUpdatedAt();
            content = message.getContent();
            channelId = messageChannelId( message );
        }
        UserDto author1 = null;
        author1 = author;
        List<BinaryContentDto> attachments1 = null;
        List<BinaryContentDto> list = attachments;
        if ( list != null ) {
            attachments1 = new ArrayList<BinaryContentDto>( list );
        }

        MessageDto messageDto = new MessageDto( id, createdAt, updatedAt, content, channelId, author1, attachments1 );

        return messageDto;
    }

    private UUID messageChannelId(Message message) {
        if ( message == null ) {
            return null;
        }
        Channel channel = message.getChannel();
        if ( channel == null ) {
            return null;
        }
        UUID id = channel.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}

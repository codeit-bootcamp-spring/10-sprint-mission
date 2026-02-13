package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-13T14:23:23+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Azul Systems, Inc.)"
)
@Component
public class MessageMapperImpl implements MessageMapper {

    @Override
    public MessageDto.Response toResponse(Message message) {
        if ( message == null ) {
            return null;
        }

        UUID id = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        String content = null;
        UUID channelId = null;
        UUID authorId = null;
        List<UUID> attachmentIds = null;

        id = message.getId();
        createdAt = message.getCreatedAt();
        updatedAt = message.getUpdatedAt();
        content = message.getContent();
        channelId = message.getChannelId();
        authorId = message.getAuthorId();
        List<UUID> list = message.getAttachmentIds();
        if ( list != null ) {
            attachmentIds = new ArrayList<UUID>( list );
        }

        MessageDto.Response response = new MessageDto.Response( id, createdAt, updatedAt, content, channelId, authorId, attachmentIds );

        return response;
    }
}

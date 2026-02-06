package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-06T16:49:58+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Azul Systems, Inc.)"
)
@Component
public class ReadStatusMapperImpl implements ReadStatusMapper {

    @Override
    public ReadStatusDto.Response toResponse(ReadStatus readStatus) {
        if ( readStatus == null ) {
            return null;
        }

        UUID id = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        UUID userId = null;
        UUID channelId = null;
        Instant lastReadAt = null;

        id = readStatus.getId();
        createdAt = readStatus.getCreatedAt();
        updatedAt = readStatus.getUpdatedAt();
        userId = readStatus.getUserId();
        channelId = readStatus.getChannelId();
        lastReadAt = readStatus.getLastReadAt();

        ReadStatusDto.Response response = new ReadStatusDto.Response( id, createdAt, updatedAt, userId, channelId, lastReadAt );

        return response;
    }
}

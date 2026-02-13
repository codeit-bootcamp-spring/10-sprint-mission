package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-13T14:23:22+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Azul Systems, Inc.)"
)
@Component
public class UserStatusMapperImpl implements UserStatusMapper {

    @Override
    public UserStatusDto.Response toResponse(UserStatus userStatus) {
        if ( userStatus == null ) {
            return null;
        }

        UUID id = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        UUID userId = null;
        Instant lastActiveAt = null;

        id = userStatus.getId();
        createdAt = userStatus.getCreatedAt();
        updatedAt = userStatus.getUpdatedAt();
        userId = userStatus.getUserId();
        lastActiveAt = userStatus.getLastActiveAt();

        UserStatusDto.Response response = new UserStatusDto.Response( id, createdAt, updatedAt, userId, lastActiveAt );

        return response;
    }
}

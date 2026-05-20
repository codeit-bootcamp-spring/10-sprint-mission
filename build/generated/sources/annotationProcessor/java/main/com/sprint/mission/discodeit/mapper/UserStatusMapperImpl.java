package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-19T18:26:07+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.4.jar, environment: Java 17.0.17 (Azul Systems, Inc.)"
)
@Component
public class UserStatusMapperImpl implements UserStatusMapper {

    @Override
    public UserStatusResponse toResponse(UserStatus userStatus) {
        if ( userStatus == null ) {
            return null;
        }

        UUID userId = null;
        Instant lastActiveAt = null;
        UUID id = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        boolean online = false;

        userId = userStatusUserId( userStatus );
        lastActiveAt = userStatus.getLastActiveAt();
        id = userStatus.getId();
        createdAt = userStatus.getCreatedAt();
        updatedAt = userStatus.getUpdatedAt();
        online = userStatus.isOnline();

        UserStatusResponse userStatusResponse = new UserStatusResponse( id, createdAt, updatedAt, userId, lastActiveAt, online );

        return userStatusResponse;
    }

    private UUID userStatusUserId(UserStatus userStatus) {
        if ( userStatus == null ) {
            return null;
        }
        User user = userStatus.getUser();
        if ( user == null ) {
            return null;
        }
        UUID id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}

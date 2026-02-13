package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-13T14:23:23+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Azul Systems, Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto.Response toResponse(User user, boolean Online) {
        if ( user == null ) {
            return null;
        }

        UUID id = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        String username = null;
        String email = null;
        UUID profileId = null;
        if ( user != null ) {
            id = user.getId();
            createdAt = user.getCreatedAt();
            updatedAt = user.getUpdatedAt();
            username = user.getUsername();
            email = user.getEmail();
            profileId = user.getProfileId();
        }
        boolean online = false;
        online = Online;

        UserDto.Response response = new UserDto.Response( id, createdAt, updatedAt, username, email, profileId, online );

        return response;
    }
}

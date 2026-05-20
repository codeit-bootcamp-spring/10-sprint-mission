package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
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
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toResponse(User user, UserStatus status, BinaryContent profileImage) {
        if ( user == null && status == null && profileImage == null ) {
            return null;
        }

        UUID id = null;
        String userName = null;
        String email = null;
        UserRole role = null;
        if ( user != null ) {
            id = user.getId();
            userName = user.getUsername();
            email = user.getEmail();
            role = user.getRole();
        }
        boolean online = false;
        Instant lastSeenAt = null;
        if ( status != null ) {
            online = status.isOnline();
            lastSeenAt = status.getLastActiveAt();
        }
        UUID profileImageId = null;
        if ( profileImage != null ) {
            profileImageId = profileImage.getId();
        }

        UserResponse userResponse = new UserResponse( id, userName, email, online, lastSeenAt, profileImageId, role );

        return userResponse;
    }

    @Override
    public UserDto toDto(User user, UserStatus status, BinaryContentDto profile) {
        if ( user == null && status == null && profile == null ) {
            return null;
        }

        UUID id = null;
        String username = null;
        String email = null;
        UserRole role = null;
        if ( user != null ) {
            id = user.getId();
            username = user.getUsername();
            email = user.getEmail();
            role = user.getRole();
        }
        Boolean online = null;
        if ( status != null ) {
            online = status.isOnline();
        }
        BinaryContentDto profile1 = null;
        profile1 = profile;

        UserDto userDto = new UserDto( id, username, email, profile1, online, role );

        return userDto;
    }
}

package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final BinaryContentMapper binaryContentMapper;

    public UserMapper() {
        this.binaryContentMapper = new BinaryContentMapper();
    }

    public UserDto toDto(User user) {
        if (user == null)
            return null;

        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                binaryContentMapper.toDto(user.getProfile()),
                user.getStatus().isOnline()
        );
    }
}

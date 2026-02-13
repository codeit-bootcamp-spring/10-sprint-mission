package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.StatusType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {


    //  User -> UserInfoDto
    public UserResponseDto toUserInfoDto(User user, StatusType status){
        Boolean isOnline = status == StatusType.ONLINE;
        return new UserResponseDto(user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getName(),
                status,
                user.getEmail(),
                user.getProfileId(),
                isOnline);
    }

    // UserInfoDto -> User
    public User toUser(UserResponseDto userResponseDto, UserRepository userRepository)
    {
        String password = userRepository.findById(userResponseDto.userId())
                .map(User::getPassword)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자 정보가 없습니다."));
        return new User(userResponseDto.userName(), userResponseDto.email(), password, userResponseDto.profileId());
    }
}

package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserInfoDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {


    //  User -> UserInfoDto
    public UserInfoDto userToUserInfoDto(User user, UserStatus userStatus){
        return new UserInfoDto(user.getName(),
                user.getId(),
                userStatus.getStatusType(),
                user.getEmail(),
                user.getProfileId());
    }

    // UserInfoDto -> User
    public User userInfoDtoToUser(UserInfoDto userInfoDto, UserRepository userRepository)
    {
        String password = userRepository.findById(userInfoDto.userId())
                .map(u -> u.getPassword())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자 정보가 없습니다."));
        return new User(userInfoDto.userName(), userInfoDto.email(), password, userInfoDto.profileId());
    }
}

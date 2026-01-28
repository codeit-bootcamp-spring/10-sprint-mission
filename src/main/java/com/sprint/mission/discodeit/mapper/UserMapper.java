package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponseDto toDto(User user){
        if(user == null) return null;

        return new UserResponseDto(user.getId()
                ,user.getName()
                ,user.getEmail()
                ,user.getProfileImageId()
                ,user.getMessageList()
                ,user.getChannelList()
                ,user.getFriendsList()
                ,true);
    }

    public User toEntity(UserResponseDto dto){
        if(dto == null) return null;

        return new User(dto.getName(), dto.getEmail(),null);
    }
}

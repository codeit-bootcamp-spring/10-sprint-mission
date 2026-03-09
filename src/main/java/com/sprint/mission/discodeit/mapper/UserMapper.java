package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

//BinaryContent  추가
@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

    //online 계산에 필요/
    @Autowired
    protected UserStatusRepository userStatusRepository;

    @Mapping(target = "profile", source = "profile")
    @Mapping(target = "online", expression = "java(mapOnline(user))")
    public abstract UserDto toDto(User user);


    // IsOnline 계산
    protected Boolean mapOnline(User user) {
        if(user == null) return false;

        return userStatusRepository.findByUser_Id(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);
    }
}

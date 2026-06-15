package com.sprint.mission.discodeit.user.presentation.mapper;

import com.sprint.mission.discodeit.common.mapper.GenericMapper;
import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.user.presentation.dto.response.UserResponse;
import com.sprint.mission.discodeit.user.domain.entity.User;
import org.mapstruct.Mapper;


@Mapper(config = GlobalMapperConfig.class)
public interface UserMapper extends GenericMapper<User, UserResponse> {

}

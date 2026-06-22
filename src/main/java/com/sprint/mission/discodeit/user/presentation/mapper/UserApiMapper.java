package com.sprint.mission.discodeit.user.presentation.mapper;

import com.sprint.mission.discodeit.binarycontent.presentation.mapper.BinaryContentApiMapper;
import com.sprint.mission.discodeit.common.mapper.GenericApiMapper;
import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.presentation.dto.response.UserResponse;
import org.mapstruct.Mapper;


@Mapper(config = GlobalMapperConfig.class, uses = BinaryContentApiMapper.class)
public interface UserApiMapper extends GenericApiMapper<User, UserResponse> {

  @Override
  UserResponse toDto(User entity);
}

package com.sprint.mission.discodeit.user.application.mapper;

import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.event.UserCreatedEvent;
import com.sprint.mission.discodeit.user.domain.event.UserDeletedEvent;
import com.sprint.mission.discodeit.user.domain.event.UserUpdatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class)
public interface UserDomainMapper {

  UserCreatedEvent toCreatedEvent(User user, String password);

  @Mapping(target = "profileId", source = "user.profile.id")
  UserUpdatedEvent toUpdatedEvent(User user, String password);

  UserDeletedEvent toDeletedEvent(User user);
}

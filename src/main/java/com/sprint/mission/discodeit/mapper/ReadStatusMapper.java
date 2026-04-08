package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.config.GlobalMapperConfig;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class)
public interface ReadStatusMapper extends BaseMapper<ReadStatus, ReadStatusDto> {

  ReadStatus toEntityFrom(ReadStatusCreateRequest request);

  List<ReadStatus> toEntityFrom(@Context Channel channel, List<User> participants);

  ReadStatus toEntityFrom(User user, @Context Channel channel);

  @AfterMapping
  default void setChannel(@MappingTarget ReadStatus.ReadStatusBuilder builder,
      @Context Channel channel) {
    builder.channel(channel);
  }

  ReadStatus partialUpdate(ReadStatusUpdateRequest request, @MappingTarget ReadStatus readStatus);
}

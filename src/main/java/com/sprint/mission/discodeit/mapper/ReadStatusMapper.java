package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ReadStatusMapper {

    @Mapping(target = "userId", source = "status.user.id")
    @Mapping(target = "channelId", source = "status.channel.id")
    ReadStatusDto toDto(ReadStatus status);

    List<ReadStatusDto> toDtoList(List<ReadStatus> statuses);
}

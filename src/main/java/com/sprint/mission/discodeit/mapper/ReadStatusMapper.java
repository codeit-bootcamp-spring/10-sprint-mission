package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.ArrayList;
import java.util.List;

public class ReadStatusMapper {
    public static ReadStatus toEntity(CreateReadStatusRequestDTO dto) {
        return new ReadStatus(
                dto.userId(),
                dto.channelId()
        );
    }

    public static ReadStatusResponseDTO toResponse(ReadStatus status) {
        return new ReadStatusResponseDTO(
                status.getId(),
                status.getUserId(),
                status.getChannelId(),
                status.getLastReadAt()
        );
    }

    public static List<ReadStatusResponseDTO> toResponseList(List<ReadStatus> statuses) {
        List<ReadStatusResponseDTO> dtos = new ArrayList<>();

        for (ReadStatus status: statuses) {
            dtos.add(ReadStatusMapper.toResponse(status));
        }

        return dtos;
    }
}

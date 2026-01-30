package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.readstatusdto.RSCreateRequestDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.RSResponseDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.Objects;

public class ReadStatusDTOMapper {
    public ReadStatus createReqToReadStatus(RSCreateRequestDTO req){
        Objects.requireNonNull(req, "유효하지 않은 요청입니다.");
        return new ReadStatus(req.userId(), req.channelId());
    }

    public RSResponseDTO rsToResponse(ReadStatus readStatus){
        return new RSResponseDTO(
                readStatus.getId(),
                readStatus.getChannelID(),
                readStatus.getUserID(),
                readStatus.getLastReadAt(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt()
        );
    }
}

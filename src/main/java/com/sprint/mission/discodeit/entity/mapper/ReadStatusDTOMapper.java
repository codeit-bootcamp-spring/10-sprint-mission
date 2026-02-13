package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ReadStatusDTOMapper {
    public ReadStatus createReqToReadStatus(ReadStatusCreateRequestDTO req){
        Objects.requireNonNull(req, "유효하지 않은 요청입니다.");
        return new ReadStatus(req.userId(), req.channelId());
    }

    public ReadStatusResponseDTO rsToResponse(ReadStatus readStatus){
        return new ReadStatusResponseDTO(
                readStatus.getId(),
                readStatus.getChannelID(),
                readStatus.getUserID(),
                readStatus.getLastReadAt(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt()
        );
    }

}

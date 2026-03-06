package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ReadStatusDTOMapper {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;


    public static ReadStatusDto rsToResponse(ReadStatus readStatus) {
        return new ReadStatusDto(
            readStatus.getId(),
            readStatus.getUser().getId(),
            readStatus.getChannel().getId(),
            readStatus.getLastReadAt()
        );
    }

}

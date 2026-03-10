package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channeldto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserMapper userMapper;

    // 시그니쳐를 바꿔보았습니다.
    public ChannelDto toDto(Channel channel, Message lastMessage) {
        return new ChannelDto(
            channel.getId(),
            channel.getType(),
            channel.getName(),
            channel.getDescription(),
            channel.getReadStatuses()
                .stream()
                .map(ReadStatus::getUser)
                .filter(Objects::nonNull)
                .map(userMapper::toDto)
                .toList(),
            // 새로 생성한 메시지가 없는 채널을 dto로 변환하면, lastMessageAt 값에는 채널 생성 날짜가 들어감.
            lastMessage != null ? lastMessage.getCreatedAt() : channel.getCreatedAt()
        );
    }
}

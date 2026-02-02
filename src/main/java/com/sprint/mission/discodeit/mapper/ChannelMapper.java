package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChannelMapper {
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    public ChannelDto toDto(Channel channel) {
        // 최신 메시지 시간 조회
        Instant lastMessageAt = messageRepository.findLatestByChannelId(channel.getId())
                .map(Message::getCreatedAt)
                .orElse(null);

        // 비공개 채널일 경우 참여자 목록 조회
        List<UUID> participantIds;
        if (channel.getType() == ChannelType.PRIVATE) {
            participantIds = readStatusRepository.findAllByChannelId(channel.getId())
                    .stream()
                    .map(ReadStatus::getUserId)
                    .toList();
        } else {
            participantIds = List.of();
        }

        return new ChannelDto(channel, lastMessageAt, participantIds);
    }

    // 참여자 목록을 알고 있을 경우 사용
    public ChannelDto toDto(Channel channel, List<UUID> participantIds) {
        return new ChannelDto(channel, null, participantIds);
    }
}
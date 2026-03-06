package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.channeldto.ChannelDto;
import com.sprint.mission.discodeit.dto.channeldto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ChannelDTOMapper {

    ReadStatusRepository readStatusRepository;
    MessageRepository messageRepository;

    public ChannelDto channelToResponseDTO(Channel channel) {

        // 채널 내에 존재하는 모든 유저들을 readStatusRepository에서 조회 후 유저 ID를 리스트로 추출.
        List<ReadStatus> participants = readStatusRepository.findAllByChannelId(channel.getId());
        List<UUID> participantsIds = participants.stream().map(r -> r.getUser().getId()).toList();

        // 채널 내에서 가장 최신의 메시지를 추출.
        Message message = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channel.getId());

        // 메시지가 존재하지 않는 메시지의 경우 NPE를 방지하기 위해 null을 리턴
        Instant lastMessageAt = message != null ? message.getCreatedAt() : null;

        return new ChannelDto(
            channel.getId(),
            channel.getType(),
            channel.getName(),
            channel.getDescription(),
            participantsIds,
            lastMessageAt
        );
    }

    public static Channel privateReqToChannel(PrivateChannelCreateDTO req) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        if (req != null && req.users() != null) {
            channel.getUserList().addAll(req.users());
        }
        return channel;
    }

    public static Channel publicReqToChannel(PublicChannelCreateDTO req) {
        return new Channel(ChannelType.PUBLIC, req.name(), req.description());
    }
}

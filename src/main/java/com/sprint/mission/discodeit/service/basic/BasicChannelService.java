package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public ChannelResponseDto createPublicChannel(ChannelCreateDto dto) {
        Channel channel = new Channel(ChannelType.PUBLIC, dto.getName(), dto.getDescription());
        channelRepository.save(channel);
        return convertToResponseDto(channel);
    }

    @Override
    public ChannelResponseDto createPrivateChannel(PrivateChannelCreateDto dto) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);

        if (dto.getMemberIds() != null) {
            for (UUID userId : dto.getMemberIds()) {
                readStatusRepository.save(new ReadStatus(userId, channel.getId()));
            }
        }
        return convertToResponseDto(channel);
    }

    @Override
    public ChannelResponseDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Not Found"));
        return convertToResponseDto(channel);
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<Channel> publicChannels = channelRepository.findByType(ChannelType.PUBLIC);

        List<UUID> privateChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .toList();

        List<Channel> privateChannels = channelRepository.findAllById(privateChannelIds);

        return Stream.concat(publicChannels.stream(), privateChannels.stream())
                .map(this::convertToResponseDto)
                .toList();
    }

    @Override
    public ChannelResponseDto update(ChannelUpdateDto dto) {
        Channel channel = channelRepository.findById(dto.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("Not Found"));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException("PRIVATE 채널은 수정 불가");
        }

        channel.update(dto.getName(), dto.getDescription());
        channelRepository.save(channel);

        return convertToResponseDto(channel);
    }

    @Override
    public void delete(UUID channelId) {
        messageRepository.deleteByChannelId(channelId);
        readStatusRepository.deleteByChannelId(channelId);
        channelRepository.deleteById(channelId);
    }

    private ChannelResponseDto convertToResponseDto(Channel channel) {
        Instant lastMessageAt = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channel.getId())
                .map(Message::getCreatedAt)
                .orElse(null);

        List<UUID> memberIds = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            memberIds = readStatusRepository.findAllByChannelId(channel.getId()).stream()
                    .map(ReadStatus::getUserId)
                    .toList();
        }

        return new ChannelResponseDto(
                channel.getId(), channel.getName(), channel.getDescription(),
                channel.getType(), lastMessageAt, memberIds
        );
    }
}
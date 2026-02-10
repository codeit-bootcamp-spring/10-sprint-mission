package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public ChannelDto create(PublicChannelCreateRequest publicChannelCreateRequest) {
        String name = publicChannelCreateRequest.name();
        String description = publicChannelCreateRequest.description();
        Channel channel = new Channel(ChannelType.PUBLIC, name, description);

        channelRepository.save(channel);

        return toDto(channel);
    }

    @Override
    public ChannelDto create(PrivateChannelCreateRequest privateChannelCreateRequest) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createdChannel = channelRepository.save(channel);

        privateChannelCreateRequest.participantIds().stream()
                .map(userId -> new ReadStatus(userId, createdChannel.getId(), Instant.MIN))
                .forEach(readStatusRepository::save);

        return toDto(createdChannel);
    }

    @Override
    public ChannelDto find(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(this::toDto)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                                || mySubscribedChannelIds.contains(channel.getId())
                )
                .map(this::toDto)
                .toList();
    }

    @Override
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest) {
        String newName = publicChannelUpdateRequest.newName();
        String newDescription = publicChannelUpdateRequest.newDescription();
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new BusinessException(ErrorCode.PRIVATE_CHANNEL_UPDATE_FORBIDDEN);
        }
        channel.update(newName, newDescription);

        channelRepository.save(channel);

        return toDto(channel);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

        messageRepository.deleteAllByChannelId(channel.getId());
        readStatusRepository.deleteAllByChannelId(channel.getId());

        channelRepository.deleteById(channelId);
    }

    private ChannelDto toDto(Channel channel) {
        Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId())
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .map(Message::getCreatedAt)
                .limit(1)
                .findFirst()
                .orElse(Instant.MIN);

        List<UUID> participantIds = new ArrayList<>();
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            readStatusRepository.findAllByChannelId(channel.getId())
                    .stream()
                    .map(ReadStatus::getUserId)
                    .forEach(participantIds::add);
        }

        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participantIds,
                lastMessageAt
        );
    }
}

package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.response.ApiException;
import com.sprint.mission.discodeit.response.ErrorCode;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusService readStatusService;
    private final MessageService messageService;
    private final UserMapper userMapper;
    private final ChannelMapper channelMapper;

    @Override
    @Transactional
    public ChannelDto createPublicChannel(PublicChannelCreateRequest request) {
        Channel channel = Channel.buildPublic(
                request.name(),
                request.description()
        );
        channelRepository.save(channel);

        return channelMapper.toDto(channel, mapParticipants(channel));
    }

    @Override
    @Transactional
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel channel = Channel.buildPrivate();
        channelRepository.save(channel);

        readStatusService.createInitialReadStatuses(
                channel.getId(),
                request.participantIds(),
                Instant.now()
        );

        return channelMapper.toDto(channel, mapParticipants(channel));
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDto findChannelByChannelId(UUID channelId) {
        Channel channel = getChannelOrThrow(channelId);
        return channelMapper.toDto(channel, mapParticipants(channel));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findAllChannelsByUserId(UUID requesterId) {
        List<Channel> channels = channelRepository.findVisibleChannels(
                requesterId,
                ChannelType.PUBLIC,
                ChannelType.PRIVATE
        );

        if (channels.isEmpty()) {
            return List.of();
        }

        return channels.stream()
                .map(channel -> channelMapper.toDto(
                        channel,
                        mapParticipants(channel)
                ))
                .toList();
    }

    @Override
    @Transactional
    public ChannelDto updateChannelInfo(UUID channelId, PublicChannelUpdateRequest request) {
        Channel channel = getChannelOrThrow(channelId);
        validateChannelType(channel);

        channel.updateInfo(
                request.newName(),
                request.newDescription()
        );

        return channelMapper.toDto(channel, mapParticipants(channel));
    }

    @Override
    @Transactional
    public void deleteChannel(UUID channelId) {
        Channel channel = getChannelOrThrow(channelId);

        for (Message message : List.copyOf(channel.getMessages())) {
            messageService.deleteMessage(message.getId());
        }
        for (ReadStatus readStatus : List.copyOf(channel.getReadStatuses())) {
            readStatusService.deleteReadStatus(readStatus.getId());
        }

        channelRepository.delete(channel);
    }

    private List<UserDto> mapParticipants(Channel channel) {
        return channel.getReadStatuses().stream()
                .map(ReadStatus::getUser)
                .map(userMapper::toDto)
                .toList();
    }

    private Channel getChannelOrThrow(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.CHANNEL_NOT_FOUND,
                        "채널을 찾을 수 없습니다 channelId: " + id
                ));
    }

    private void validateChannelType(Channel channel) {
        if (channel.isPrivate()) {
            throw new ApiException(
                    ErrorCode.CHANNEL_UPDATE_FORBIDDEN,
                    "PRIVATE 채널은 수정할 수 없습니다. channelId: " + channel.getId()
            );
        }
    }
}

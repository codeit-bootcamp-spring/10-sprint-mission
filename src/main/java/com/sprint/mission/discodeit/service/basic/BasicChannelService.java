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
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
        log.debug("Public 채널 생성 처리 시작: name={}", request.name());
        Channel channel = Channel.buildPublic(
                request.name(),
                request.description()
        );
        channelRepository.save(channel);
        log.info("Public 채널 생성 완료: channelId={}, name={}", channel.getId(), channel.getName());

        return channelMapper.toDto(channel, mapParticipants(channel));
    }

    @Override
    @Transactional
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {
        log.debug("Private 채널 생성 처리 시작: participantIds={}", request.participantIds());
        Channel channel = Channel.buildPrivate();
        channelRepository.save(channel);

        readStatusService.createInitialReadStatuses(
                channel.getId(),
                request.participantIds(),
                Instant.now()
        );
        log.info("Private 채널 생성 완료: channelId={}, 참여자 수={}", channel.getId(),
                request.participantIds().size());

        return channelMapper.toDto(channel, mapParticipants(channel));
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDto findChannelByChannelId(UUID channelId) {
        log.debug("채널 조회: channelId={}", channelId);
        Channel channel = getChannelOrThrow(channelId);
        return channelMapper.toDto(channel, mapParticipants(channel));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findAllChannelsByUserId(UUID requesterId) {
        log.debug("사용자별 채널 목록 조회: userId={}", requesterId);
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
        log.debug("채널 수정 처리 시작: channelId={}", channelId);
        Channel channel = getChannelOrThrow(channelId);
        validateChannelType(channel);

        channel.updateInfo(
                request.newName(),
                request.newDescription()
        );
        log.info("채널 수정 완료: channelId={}, newName={}", channelId, request.newName());

        return channelMapper.toDto(channel, mapParticipants(channel));
    }

    @Override
    @Transactional
    public void deleteChannel(UUID channelId) {
        log.debug("채널 삭제 처리 시작: channelId={}", channelId);
        Channel channel = getChannelOrThrow(channelId);

        for (Message message : List.copyOf(channel.getMessages())) {
            messageService.deleteMessage(message.getId());
        }
        for (ReadStatus readStatus : List.copyOf(channel.getReadStatuses())) {
            readStatusService.deleteReadStatus(readStatus.getId());
        }

        channelRepository.delete(channel);
        log.info("채널 삭제 완료: channelId={}", channelId);
    }

    private List<UserDto> mapParticipants(Channel channel) {
        return channel.getReadStatuses().stream()
                .map(ReadStatus::getUser)
                .map(userMapper::toDto)
                .toList();
    }

    private Channel getChannelOrThrow(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new ChannelNotFoundException(
                        "채널을 찾을 수 없습니다 channelId: " + id,
                        Map.of("channelId", id)
                ));
    }

    private void validateChannelType(Channel channel) {
        if (channel.isPrivate()) {
            log.warn("Private 채널 수정 시도: channelId={}", channel.getId());
            throw new PrivateChannelUpdateException(
                    "PRIVATE 채널은 수정할 수 없습니다. channelId: " + channel.getId(),
                    Map.of("channelId", channel.getId(), "channelType", channel.getType().name())
            );
        }
    }
}

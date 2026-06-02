package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelParticipantException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateNotAllowedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;
    private final UserMapper userMapper;

    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    public ChannelDto.Response create(ChannelDto.PublicChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        Channel savedChannel = channelRepository.save(channel);

        log.info("[Channel Created] Type: PUBLIC, Name: {}, ID: {}", savedChannel.getName(), savedChannel.getId());
        return toDto(savedChannel);
    }

    @Transactional
    public ChannelDto.Response create(ChannelDto.PrivateChannelCreateRequest request) {
        List<User> participants = userRepository.findAllById(request.participantIds());

        Set<UUID> foundIds = participants.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        Set<UUID> missingIds = request.participantIds().stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toSet());

        if (!missingIds.isEmpty()) {
            throw UserNotFoundException.withIds(missingIds);
        }

        if (participants.size() < 2) {
            throw PrivateChannelParticipantException.minimumParticipants(participants.size());
        }


        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel savedChannel = channelRepository.save(channel);

        participants.stream()
                .map(user -> new ReadStatus(user, savedChannel, savedChannel.getCreatedAt()))
                .forEach(readStatusRepository::save);

        log.info("[Channel Created] Type: PRIVATE, ID: {}, Participants: {} members",
                savedChannel.getId(), participants.size());

        return toDto(savedChannel);
    }

    @Override
    public ChannelDto.Response find(UUID channelId) {
        ChannelDto.Response response = channelRepository.findById(channelId)
                .map(this::toDto)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId));

        log.debug("[Channel Found] ID: {}, Name: {}, Type: {}", channelId, response.name(), response.type());

        return response;
    }

    @Override
    public List<ChannelDto.Response> findAllByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException.withId(userId);
        }
        List<Channel> channels = channelRepository.findAllAccessibleByUserId(userId);

        log.debug("[Channels Fetched] User: {}, Accessible Channels: {}", userId, channels.size());

        return toDtos(channels);
    }

    @Override
    public List<ChannelDto.Response> findAll() {
        List<Channel> channels = channelRepository.findAll();
        log.debug("[Channels Fetched] Total Global Channels: {}", channels.size());
        return toDtos(channels);
    }

    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Override
    @Transactional
    public ChannelDto.Response update(UUID channelId, ChannelDto.UpdatePublicRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw PrivateChannelUpdateNotAllowedException.withId(channelId);
        }
        String oldName = channel.getName();
        channel.update(request.newName(), request.newDescription());

        log.info("[Channel Updated] ID: {}, Name: {} -> {}", channelId, oldName, request.newName());
        return toDto(channel);
    }

    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Override
    @Transactional
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId));
        channelRepository.delete(channel);
        log.info("[Channel Deleted] ID: {}, Name: {}, Type: {}", channelId, channel.getName(), channel
                .getType());
    }

    // Helper
    private ChannelDto.Response toDto(Channel channel) {
        List<UserDto.Response> participants = new ArrayList<>();
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            participants = readStatusRepository.findAllByChannelId(channel.getId())
                    .stream()
                    .map(ReadStatus::getUser)
                    .map(userMapper::toResponse)
                    .toList();
        }

        return channelMapper.toResponse(channel, participants);
    }
    private List<ChannelDto.Response> toDtos(List<Channel> channels) {
        if (channels.isEmpty()) return List.of(); // 채널 하나도 없을 때 early 리턴으로 DB 접속 아끼기

        List<UUID> channelIds = channels.stream()
                .map(Channel::getId)
                .toList();

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdsWithUser(channelIds);

        Map<UUID, List<UserDto.Response>> participantsMap = readStatuses.stream()
                .collect(Collectors.groupingBy(
                        rs -> rs.getChannel().getId(),
                        Collectors.mapping(
                                rs -> userMapper.toResponse(rs.getUser()),
                                Collectors.toList()
                        )
                ));

        return channels.stream()
                .map(channel -> channelMapper.toResponse(
                        channel,
                        (channel.getType() == ChannelType.PRIVATE)
                                ? participantsMap.getOrDefault(channel.getId(), List.of())
                                : List.of()
                ))
                .toList();
    }

}

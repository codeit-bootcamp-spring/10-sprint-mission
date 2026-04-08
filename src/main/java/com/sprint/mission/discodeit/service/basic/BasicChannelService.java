package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.global.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.global.InvalidInputException;
import com.sprint.mission.discodeit.exception.global.UnchangedValueException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BasicChannelService implements ChannelService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    private final ChannelMapper channelMapper;
    private final UserMapper userMapper;

    @Override
    public ChannelDto createPublicChannel(CreatePublicChannelRequestDTO dto) {
        Channel channel = new Channel(dto.name(), dto.description(), ChannelType.PUBLIC);
        channelRepository.save(channel);

        for (User user: userRepository.findAll()) {
            ReadStatus readStatus = new ReadStatus(user, channel);

            readStatusRepository.save(readStatus);
        }

        log.info("[PUBLIC_CHANNEL_CREATE_SUCCESS] 공개 채널 생성 성공: channelId={}", channel.getId());
        return buildSingleChannelDto(channel);
    }

    @Override
    public ChannelDto createPrivateChannel(CreatePrivateChannelRequestDTO dto) {
        // Mapper에서 name, description은 null로 처리
        Channel channel = new Channel(null, null, ChannelType.PRIVATE);
        channelRepository.save(channel);

        // id를 List로 한번에 조회 -> n+1 문제 방지를 위함
        List<User> users = userRepository.findAllById(dto.participantIds());
        // dto의 리스트와 실제 db 리스트가 맞지 않는 경우 -> 존재하지 않는 사용자가 있는 경우 처리
        if (users.size() != dto.participantIds().size()) {
            throw new UserNotFoundException("존재하지 않는 사용자가 있습니다.");
        }

        for (User user: users) {
            ReadStatus readStatus = new ReadStatus(user, channel);

            readStatusRepository.save(readStatus);
        }

        log.info("[PRIVATE_CHANNEL_CREATE_SUCCESS] 비공개 채널 생성 성공: channelId={}", channel.getId());
        return buildSingleChannelDto(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findAllByUserId(UUID userId) {
        findUserOrThrow(userId);

        List<Channel> publicChannels = channelRepository.findAllByType(ChannelType.PUBLIC);
        List<Channel> privateChannels = readStatusRepository.findAllByUser_Id(userId).stream()
                .map(ReadStatus::getChannel)
                .filter(channel -> channel.getType().equals(ChannelType.PRIVATE))
                .distinct()
                .toList();

        List<Channel> channels = Stream.concat(publicChannels.stream(), privateChannels.stream())
                                .distinct()
                                .toList();

        Map<UUID, List<UserDto>> participantsMap = getParticipantsMap(channels);
        Map<UUID, Instant> lastMessageAtMap = getLastMessageAtMap(channels);

        return channels.stream()
                .map(channel -> toChannelDto(channel, participantsMap, lastMessageAtMap))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDto findByChannelId(UUID channelId) {
        Channel channel = findChannelOrThrow(channelId);

        return buildSingleChannelDto(channel);
    }

    @Override
    public ChannelDto updateChannel(
            UUID channelId,
            UpdateChannelRequestDTO dto
    ) {
        Channel channel = findChannelOrThrow(channelId);

        if (channel.getType() == ChannelType.PRIVATE) {
            log.warn("[CHANNEL_UPDATE_FAIL_BY_CHANNEL_TYPE] 비공개 채널로 채널 정보 수정 실패: channelId={}", channelId);
            throw new PrivateChannelUpdateException(channelId);
        }

        if (dto.newName() != null) {
            updateChannelName(dto, channel);
        }
        if (dto.newDescription() != null) {
            updateChannelDescription(dto, channel);
        }

        log.info("[CHANNEL_UPDATE_SUCCESS] 채널 정보 수정 성공: channelId={}", channelId);
        return buildSingleChannelDto(channel);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        findChannelOrThrow(channelId);

        log.info("[CHANNEL_DELETE_SUCCESS] 채널 삭제 성공: channelId={}", channelId);
        messageRepository.deleteAllByChannel_Id(channelId);
        channelRepository.deleteById(channelId);
    }

    private Channel findChannelOrThrow(UUID channelId) {
        if (channelId == null) {
            throw new InvalidInputException(
                    ErrorCode.ID_CAN_NOT_BE_NULL, Map.of("channelId", "channelId is null")
            );
        }

        return channelRepository.findById(channelId)
                .orElseThrow(() ->
                {
                    log.warn("[CHANNEL_NOT_FOUND] 채널이 존재하지 않음: channelId={}", channelId);
                    return new ChannelNotFoundException(channelId);
                });
    }

    private User findUserOrThrow(UUID userId) {
        if (userId == null) {
            throw new InvalidInputException(
                    ErrorCode.ID_CAN_NOT_BE_NULL, Map.of("userId", "userId is null")
            );
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[USER_NOT_FOUND] 유저가 존재하지 않음: userId={}", userId);
                    return new UserNotFoundException(userId);
                });
    }

    private void updateChannelName(UpdateChannelRequestDTO dto, Channel channel) {
        if (!dto.newName().equals(channel.getName())) {
            if (channelRepository.existsByName(dto.newName())) {
                log.warn("[CHANNEL_UPDATE_FAIL_BY_CHANNELNAME] 이미 사용중인 채널 이름으로 수정 시도로 채널 정보 수정 실패: channelId={}", channel.getId());
                throw new DuplicateResourceException(
                        ErrorCode.CHANNEL_NAME_ALREADY_EXISTS, Map.of("channelId", channel.getId())
                );
            }
        }

        if (dto.newName().isBlank()) {
            throw new InvalidInputException(
                    ErrorCode.CHANNEL_NAME_IS_BLANK, Map.of("channelName", "")
            );
        }

        channel.updateChannelName(dto.newName());
    }

    private void updateChannelDescription(UpdateChannelRequestDTO dto, Channel channel) {
        if (dto.newDescription().equals(channel.getDescription())) {
            log.warn("[CHANNEL_UPDATE_FAIL_BY_DESCRIPTION] 같은 설명으로 수정 시도로 채널 정보 수정 실패: channelId={}", channel.getId());
            throw new UnchangedValueException(
                    ErrorCode.CHANNEL_DESCRIPTION_UNCHANGED, Map.of("channelId", channel.getId())
            );
        }

        if (dto.newDescription().isBlank()) {
            throw new InvalidInputException(
                    ErrorCode.CHANNEL_DESCRIPTION_IS_BLANK, Map.of("description", "")
            );
        }

        channel.updateDescription(dto.newDescription());
    }

    private ChannelDto toChannelDto(
            Channel channel,
            Map<UUID, List<UserDto>> participantsMap,
            Map<UUID, Instant> lastMessageAtMap
    ) {
        return channelMapper.toDto(
                channel,
                participantsMap.getOrDefault(channel.getId(), List.of()),
                lastMessageAtMap.get(channel.getId())
        );
    }

    private ChannelDto buildSingleChannelDto(Channel channel) {
        List<Channel> channels = List.of(channel);
        Map<UUID, List<UserDto>> participantsMap = getParticipantsMap(channels);
        Map<UUID, Instant> lastMessageAtMap = getLastMessageAtMap(channels);

        return toChannelDto(channel, participantsMap, lastMessageAtMap);
    }

    // 채널 목록용 집계 메서드 map -> uuid로 한 번에 찾으려고
    private Map<UUID, List<UserDto>> getParticipantsMap(List<Channel> channels) {
        List<UUID> channelIds = channels.stream()
                .map(Channel::getId)
                .toList();

        if (channelIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannel_IdIn(channelIds);

        return readStatuses.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        rs -> rs.getChannel().getId(),
                        java.util.stream.Collectors.collectingAndThen(
                                java.util.stream.Collectors.toMap(
                                        rs -> rs.getUser().getId(),
                                        rs -> userMapper.toDto(rs.getUser()),
                                        (a, b) -> a,
                                        LinkedHashMap::new
                                ),
                                map -> new ArrayList<>(map.values())
                        )
                ));
    }

    // 마지막 메시지 시간 map -> uuid로 한 번에 찾으려고
    private Map<UUID, Instant> getLastMessageAtMap(List<Channel> channels) {
        List<UUID> channelIds = channels.stream()
                .map(Channel::getId)
                .toList();

        if (channelIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Object[]> results = messageRepository.findLastMessageAtByChannelIds(channelIds);

        Map<UUID, Instant> lastMessageAtMap = new HashMap<>();

        for (Object[] row : results) {
            UUID channelId = (UUID) row[0];
            Instant createdAt = (Instant) row[1];
            lastMessageAtMap.put(channelId, createdAt);
        }

        return lastMessageAtMap;
    }
}

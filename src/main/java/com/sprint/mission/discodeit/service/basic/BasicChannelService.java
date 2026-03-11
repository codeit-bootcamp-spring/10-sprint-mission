package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    //
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;
    private final UserMapper userMapper;

    @Override
    public ChannelDto create(PublicChannelCreateRequest request) {

        String name = request.name();
        String description = request.description();

        Channel channel = new Channel(ChannelType.PUBLIC, name, description);
        Channel savedChannel = channelRepository.save(channel);

        List<User> users = userRepository.findAll();

        for (User user : users) {
            ReadStatus readStatus = new ReadStatus(user, savedChannel, Instant.now());
            readStatusRepository.save(readStatus);
        }

        // participants 생성
        List<UserDto> participants = users.stream()
                .map(userMapper::toDto)
                .toList();

        Map<UUID, List<UserDto>> participantsMap =
                Map.of(savedChannel.getId(), participants);

        Map<UUID, Instant> lastMessageMap =
                Map.of(savedChannel.getId(), null);

        return toDto(savedChannel, lastMessageMap, participantsMap);
    }

    @Override
    public ChannelDto create(PrivateChannelCreateRequest request) {

        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel savedChannel = channelRepository.save(channel);

        List<UserDto> participants = request.participantIds().stream()
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(userId + "에 해당하는 User가 없습니다.")))
                .peek(user -> {
                    ReadStatus readStatus =
                            new ReadStatus(user, savedChannel, savedChannel.getCreatedAt());
                    readStatusRepository.save(readStatus);
                })
                .map(userMapper::toDto)
                .toList();

        Map<UUID, List<UserDto>> participantsMap =
                Map.of(savedChannel.getId(), participants);

        Map<UUID, Instant> lastMessageMap =
                Map.of(savedChannel.getId(), null);

        return toDto(savedChannel, lastMessageMap, participantsMap);
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDto find(UUID channelId) {

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId + " 에 해당하는 채널이 없습니다."));

        List<UserDto> participants =
                readStatusRepository.findAllByChannelId(channelId).stream()
                        .map(ReadStatus::getUser)
                        .map(userMapper::toDto)
                        .toList();

        Map<UUID, List<UserDto>> participantsMap =
                Map.of(channelId, participants);

        Instant lastMessageAt =
                messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channelId)
                        .map(Message::getCreatedAt)
                        .orElse(null);

        Map<UUID, Instant> lastMessageMap =
                Map.of(channelId, lastMessageAt);

        return toDto(channel, lastMessageMap, participantsMap);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findAllByUserId(UUID userId) {

        List<Channel> channels = channelRepository.findAll();

        List<UUID> channelIds = channels.stream()
                .map(Channel::getId)
                .toList();

        Map<UUID, Instant> lastMessageMap = messageRepository.findLastMessageAtByChannelIds(channelIds).stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row-> (Instant) row[1]
                ));


        Map<UUID, List<UserDto>> participantsMap =
                readStatusRepository.findAllByChannelIdIn(channelIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                ReadStatus -> ReadStatus.getChannel().getId(),
                                Collectors.mapping(
                                        ReadStatus -> userMapper.toDto(ReadStatus.getUser()),
                                        Collectors.toList()
                                )
                        ));

        return channels.stream()
                .map(channel -> toDto(channel, lastMessageMap, participantsMap))
                .toList();

    }

    @Override
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {

        String newName = request.newName();
        String newDescription = request.newDescription();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException("Channel with " + channelId + " not found."));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("Private channel cannot be updated");
        }

        channel.update(newName, newDescription); // Dirty Checking

        // participants 조회
        List<UserDto> participants =
                readStatusRepository.findAllByChannelId(channelId).stream()
                        .map(ReadStatus::getUser)
                        .map(userMapper::toDto)
                        .toList();

        Map<UUID, List<UserDto>> participantsMap =
                Map.of(channelId, participants);

        // 마지막 메시지 조회
        Instant lastMessageAt =
                messageRepository
                        .findTopByChannelIdOrderByCreatedAtDesc(channelId)
                        .map(Message::getCreatedAt)
                        .orElse(null);

        Map<UUID, Instant> lastMessageMap =
                Map.of(channelId, lastMessageAt);

        return toDto(channel, lastMessageMap, participantsMap);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException("Channel with" + channelId + " not found."));

        messageRepository.deleteAllByChannelId(channel.getId());
        readStatusRepository.deleteAllByChannelId(channel.getId());
        channelRepository.deleteById(channelId);
    }

    private ChannelDto toDto(Channel channel,Map<UUID, Instant> lastMessageMap,Map<UUID,List<UserDto>> participantsMap) {
        ChannelDto baseDto = channelMapper.toDto(channel);

        Instant lastMessageAt = lastMessageMap.getOrDefault(channel.getId(),null);
        List<UserDto> participants = participantsMap.getOrDefault(channel.getId(),List.of());

        return new ChannelDto(
                baseDto.id(),
                baseDto.type(),
                baseDto.name(),
                baseDto.description(),
                participants,
                lastMessageAt
        );
    }
}

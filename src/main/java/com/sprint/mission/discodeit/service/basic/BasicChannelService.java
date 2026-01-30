package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public ChannelResponseDTO createPublicChannel(CreatePublicChannelRequestDTO dto) {
        Objects.requireNonNull(dto.channelName(), "공개 채널의 이름은 null일 수 없습니다.");

        Channel channel = ChannelMapper.toPublicChannelEntity(dto);
        channelRepository.save(channel);
        return ChannelMapper.toResponse(channel);
    }

    @Override
    public ChannelResponseDTO createPrivateChannel(CreatePrivateChannelRequestDTO dto) {
        Objects.requireNonNull(dto.joinedUserIds(), "joinedUserIds는 null값일 수 없습니다.");
        if (dto.joinedUserIds().isEmpty()) {
            throw new IllegalArgumentException("비공개 채널엔 최소 1명 이상 입장하여야 합니다.");
        }
        Instant currentTime = Instant.now();
        // 유저 검증
        dto.joinedUserIds().forEach(id -> findUserOrThrow(id));

        Channel channel = ChannelMapper.toPrivateChannelEntity("");

        for (UUID ids: dto.joinedUserIds()) {
            channel.addUser(ids);
        }

        channelRepository.save(channel);

        for (UUID userId: channel.getJoinedUserIds()) {
            ReadStatus readStatus = new ReadStatus(userId, channel.getId(), currentTime);
            readStatusRepository.save(readStatus);
        }

        return ChannelMapper.toResponse(channel);
    }

    @Override
    public List<ChannelWithLastMessageDTO> findAllByUserId(UUID userId) {
        findUserOrThrow(userId);
        List<Channel> allChannels = channelRepository.findAll();
        List<Channel> publicChannels
                = allChannels.stream()
                .filter(channel -> channel.getChannelType().equals(ChannelType.PUBLIC))
                .toList();
        List<Channel> privateChannels
                = allChannels.stream()
                .filter(channel -> channel.getChannelType().equals(ChannelType.PRIVATE))
                .filter(channel -> channel.getJoinedUserIds().contains(userId))
                .toList();

        return withLastMessageDTOS(publicChannels, privateChannels);
    }

    @Override
    public ChannelWithLastMessageDTO findByChannelId(UUID channelId) {
        Channel channel = findChannelOrThrow(channelId);
        Instant lastMessageAt = findLatestMessageAt(channelId);

        List<UUID> joinUserIds = channel.getChannelType() == ChannelType.PRIVATE
                ? channel.getJoinedUserIds()
                : List.of();

        return new ChannelWithLastMessageDTO(
                channel.getId(),
                channel.getChannelName(),
                channel.getChannelType(),
                joinUserIds,
                lastMessageAt
        );
    }

    @Override
    public ChannelWithLastMessageDTO updateChannelName(
            UpdateChannelRequestDTO dto
    ) {
        Channel channel = findChannelOrThrow(dto.channelId());

        if (channel.getChannelType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("비공개 채널은 채널 이름을 바꿀 수 없습니다.");
        }

        channel.updateChannelName(dto.newChannelName());
        channelRepository.save(channel);

        return ChannelMapper.toWithLastMessage(channel, findLatestMessageAt(dto.channelId()));
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = findChannelOrThrow(channelId);
        User user = findUserOrThrow(userId);

        if (channel.getJoinedUserIds().stream()
                .anyMatch(id -> id.equals(userId))) {
            throw new IllegalArgumentException("해당 채널에 이미 참여하였습니다.");
        }

        // 메모리 갱신
        channel.addUser(user.getId());
        user.updateJoinedChannels(channel.getId());
        // file 갱신
        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = findChannelOrThrow(channelId);
        User user = findUserOrThrow(userId);

        if (channel.getJoinedUserIds().stream()
                .noneMatch(id -> id.equals(userId))) {
            throw new IllegalArgumentException("해당 채널에 참여하고 있지 않습니다.");
        }

        channel.removeUser(user.getId());
        user.removeChannel(channel.getId());

        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        findChannelOrThrow(channelId);
        readStatusRepository.deleteByChannelId(channelId);
        messageRepository.deleteByChannelId(channelId);
        channelRepository.deleteById(channelId);
    }

    private Channel findChannelOrThrow(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");

        return channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 id를 가진 채널이 존재하지 않습니다."));
    }

    private User findUserOrThrow(UUID userId) {
        Objects.requireNonNull(userId, "userId는 null값일 수 없습니다.");

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 id를 가진 사용자가 존재하지 않습니다."));
    }

    private Instant findLatestMessageAt(UUID channelId) {
        findChannelOrThrow(channelId);

        List<Message> messages = messageRepository.findByChannelId(channelId);
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        return messages.stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);
    }

    private List<ChannelWithLastMessageDTO> withLastMessageDTOS(
            List<Channel> publicChannels, List<Channel> privateChannels) {
        return Stream.concat(
                        publicChannels.stream(),   // PUBLIC 채널 리스트를 스트림으로 변환
                        privateChannels.stream()   // PRIVATE 채널 리스트를 스트림으로 변환
                )
                // 이제 두 스트림이 하나로 합쳐짐 (PUBLIC + PRIVATE)
                .map(channel -> {
                    // 각 채널마다 가장 최근 메시지 시간 계산
                    Instant lastMessageAt = findLatestMessageAt(channel.getId());

                    // 채널 타입에 따라 joinedUserIds를 다르게 내려줌
                    // PRIVATE → 참여자 목록
                    // PUBLIC  → 빈 리스트
                    List<UUID> joinedUserIdsToReturn =
                            channel.getChannelType() == ChannelType.PRIVATE
                                    ? channel.getJoinedUserIds()
                                    : List.of();

                    // 최종적으로 조회용 DTO 생성해서 반환
                    return new ChannelWithLastMessageDTO(
                            channel.getId(),
                            channel.getChannelName(),
                            channel.getChannelType(),
                            joinedUserIdsToReturn,
                            lastMessageAt
                    );
                })

                // 모든 DTO를 List로 모아서 반환
                .toList();
    }
}

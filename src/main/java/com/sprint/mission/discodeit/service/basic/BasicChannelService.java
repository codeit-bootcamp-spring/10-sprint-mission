package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService{
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    // PUBLIC 채널 생성
    @Override
    public ChannelResponse createPublicChannel(PublicChannelCreateRequest request){
        Channel channel = new Channel(
                request.name(),
                request.description(),
                request.type(),
                true
        );
        channelRepository.save(channel);
        return convertToResponse(channel, null, null);
    }

    // PRIVATE 채널 생성
    @Override
    public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(null, null, request.type(), false);
        channelRepository.save(channel);

        // 참여 유저별 ReadStatus 정보 생성
        request.memberIds().forEach(memberId -> {
            validateUserExists(memberId);

            ReadStatus readStatus = new ReadStatus(memberId, channel.getId(), Instant.now());
            readStatusRepository.save(readStatus);
        });

        return convertToResponse(channel, null, request.memberIds());
    }

    // 단건 조회
    @Override
    public ChannelResponse getChannelById(UUID id) {
        Channel channel = getOrThrowChannel(id);
        return convertToResponse(channel, getLastMessageAt(id), getMemberIdsIfPrivate(channel));
    }

    // 유저별 참여하고 있는 채널 전체 조회
    @Override
    public List<ChannelResponse> getAllByUserId(UUID userId) {
        // PUBLIC 채널 조회
        List<Channel> publicChannels = channelRepository.findAllPublic();
        // PRIVATE 채널 조회
        List<Channel> privateChannels = readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatus -> channelRepository.findById(readStatus.getChannelId()).orElse(null))
                .filter(Objects::nonNull)
                .filter(channel -> !channel.isPublic())
                .toList();

        // 두 목록 합치기
        List<Channel> allChannels = new ArrayList<>();
        allChannels.addAll(publicChannels);
        allChannels.addAll(privateChannels);

        return allChannels.stream()
                .map(channel -> {
                    Instant lastMessageAt = getLastMessageAt(channel.getId());
                    List<UUID> memberIds = getMemberIdsIfPrivate(channel);

                    return convertToResponse(channel, lastMessageAt, memberIds);
                })
                .toList();
    }

    // 채널 정보 수정
    @Override
    public ChannelResponse update(UUID id, ChannelUpdateRequest request) {
        Channel channel = getOrThrowChannel(id);

        // PRIVATE 채널은 수정할 수 없음
        if (!channel.isPublic()) {
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        Optional.ofNullable(request.name()).ifPresent(channel::updateName);
        Optional.ofNullable(request.description()).ifPresent(channel::updateDescription);

        channelRepository.save(channel);
        return convertToResponse(channel, getLastMessageAt(id), getMemberIdsIfPrivate(channel));
    }

    // 채널 삭제
    @Override
    public void deleteByChannelId(UUID id) {
        messageRepository.deleteByChannelId(id);
        readStatusRepository.deleteByChannelId(id);
        channelRepository.deleteById(id);
    }


    // 특정 채널의 가장 최근 메시지 시각 조회
    private Instant getLastMessageAt(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder()) // 가장 큰 값(최근 시간) 찾기
                .orElse(null);
    }

    // 비공개 채널인 경우 참여자 ID 목록 조회
    private List<UUID> getMemberIdsIfPrivate(Channel channel) {
        if (channel.isPublic()) {
            return null;
        }
        return readStatusRepository.findAllByChannelId(channel.getId()).stream()
                .map(ReadStatus::getUserId)
                .toList();
    }

    // 채널 검증
    private Channel getOrThrowChannel(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다."));
    }

    // 유저 검증
    private void validateUserExists(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
    }

    // 엔티티 -> DTO 변환
    private ChannelResponse convertToResponse(Channel channel, Instant lastMessageAt, List<UUID> memberIds) {
        return new ChannelResponse(
                channel.getId(),
                channel.getName(),
                channel.getDescription(),
                channel.getType(),
                channel.isPublic(),
                lastMessageAt,
                memberIds,
                channel.getCreatedAt(),
                channel.getUpdatedAt()
        );
    }
}

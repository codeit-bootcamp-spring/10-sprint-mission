package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public ChannelResponse createPublic(PublicChannelCreateRequest request) {
        if(channelRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("이미 존재하는 공개 채널 이름(name)입니다. " + request.name());
        }
        // PUBLIC 채널 생성
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        Channel savedChannel = channelRepository.save(channel);

        return toResponse(savedChannel);

        // TODO: Channel 이름 유효성 검사 로직 추가
        // TODO: description 이름 유효성 검사 로직 추가
    }

    @Override
    public ChannelResponse createPrivate(PrivateChannelCreateRequest request) {
        Set<UUID> requestedUserIds = request.participantIds();

        channelRepository.findPrivateChannelByParticipants(requestedUserIds).ifPresent(existingChannel -> {
            throw new IllegalArgumentException("동일한 유저로 구성된 비공개 채널이 존재합니다.");
        });

        // PRIVATE 채널 생성 (이름, 설명 없음)
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel savedChannel = channelRepository.save(channel);

        // 참여자별 ReadStatus 생성
        for (UUID userId : request.participantIds()) {
            // 유저 존재 여부 확인 (데이터 무결성)
            if (userRepository.existsById(userId)) {
                ReadStatus readStatus = new ReadStatus(savedChannel.getId(), userId);
                readStatusRepository.save(readStatus);
            } else {
                throw new NoSuchElementException("유저가 존재하지 않습니다." + userId);
            }
        }
        return toResponse(savedChannel);
    }

    @Override
    public ChannelResponse find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다. id: " + channelId));
        return toResponse(channel);
    }

    // 특정 유저가 볼 수 있는 Channel 목록을 조회
    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        // 모든 채널 조회 -> PUBLIC 채널은 모두 포함 / PRIVATE 채널은 해당 유저가 ReadStatus를 가지고 있는 경우만 포함
        // TODO: ChannelUser 관계 매핑 엔티티 제작 필요할지 고려 요망 / ReadStatus을 활용할 건지도 고려

        return channelRepository.findAll().stream()
                .filter(channel -> {
                    if (channel.getType() == ChannelType.PUBLIC) {
                        return true;
                    } else {
                        // PRIVATE 채널인 경우, 유저가 해당 채널에 대한 ReadState를 가지고 있는지 확인
                        return readStatusRepository.findAll().stream()
                                .anyMatch(rs -> rs.getChannelId().equals(channel.getId())
                                        && rs.getUserId().equals(userId));
                    }
                })
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ChannelResponse update(UUID channelId, ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다. id: " + channelId));

        // PRIVATE 채널은 수정 불가
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("비공개(PRIVATE) 채널은 수정할 수 없습니다. name, description 수정 불가능.");
        }

        channel.update(request.name(), request.description());
        Channel savedChannel = channelRepository.save(channel);

        return toResponse(savedChannel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("해당 채널이 존재하지 않습니다. id: " + channelId);
        }

        // 1 관련 메시지 삭제
        // JCF Repo 특성상 반복문으로 돌면서 삭제시 ConcurrentModificationException 발생 가능 -> id 수집 후 삭제
        List<UUID> messageIdsToDelete = messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .map(Message::getId)
                .toList();
        messageIdsToDelete.forEach(messageRepository::deleteById);

        // 2 관련 ReadStatus 삭제
        List<UUID> readStatusIdsToDelete = readStatusRepository.findAll().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .map(ReadStatus::getId)
                .toList();
        readStatusIdsToDelete.forEach(readStatusRepository::deleteById);

        // 3 채널 삭제
        channelRepository.deleteById(channelId);
    }


    // Entity -> DTO
    private ChannelResponse toResponse(Channel channel) {
        // 해당 채널의 가장 최근 메시지 시간 조회
        Instant lastMessageAt = messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channel.getId()))
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null); // 메시지가 없으면 null

        // PRIVATE 채널인 경우 참여자 id 목록 조회
        List<UUID> participantIds = null;
        if(channel.getType() == ChannelType.PRIVATE) {
            participantIds = readStatusRepository.findAll().stream()
                    .filter(rs -> rs.getChannelId().equals(channel.getId()))
                    .map(ReadStatus::getUserId)
                    .collect(Collectors.toList());
        }

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                lastMessageAt,
                participantIds
        );
    }
}

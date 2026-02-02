package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request) {
        // 비공개 채널 생성을 위한 필수 검증
        validateCreatePrivateRequest(request);

        // 비공개 채널 생성 및 저장
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);

        // 비공개 채널 참여자의 읽음 상태를 초기값으로 생성 및 저장
        for (UUID userId : request.userIds()) {
            ReadStatus readStatus = new ReadStatus(userId, channel.getId(), Instant.EPOCH);
            readStatusRepository.save(readStatus);
        }

        return ChannelResponse.from(channel, null, List.copyOf(request.userIds()));
    }

    @Override
    public ChannelResponse createPublicChannel(PublicChannelCreateRequest request) {
        // 공개 채널 생성을 위한 필수 검증
        validateCreatePublicRequest(request);

        // 공개 채널 생성 및 저장
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        channelRepository.save(channel);

        return ChannelResponse.from(channel, null, List.of());
    }

    @Override
    public ChannelResponse findChannelById(UUID channelId) {
        if (channelId == null) {
            throw new RuntimeException("채널이 존재하지 않습니다.");
        }

        // 채널이 존재하는지 검색 및 검증
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("채널이 존재하지 않습니다."));

        // 응답 DTO 생성 및 반환
        return toResponse(channel);
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        if (userId == null) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }

        // 전체 채널 목록 조회
        List<Channel> channels = channelRepository.findAll();

        // 유저가 속한 채널 목록 조회
        Set<UUID> joinedPrivateChannelIds = new HashSet<>();
        readStatusRepository.findAllByUserId(userId)
                .forEach(rs -> joinedPrivateChannelIds.add(rs.getChannelId()));

        
        List<ChannelResponse> responses = new ArrayList<>();
        // 전체 채널 목록을 순회
        for (Channel channel : channels) {
            // 비공개 채널일 경우 참, 공개 채널일 경우 거짓
            if (channel.getType() == ChannelType.PRIVATE) {
                // 유저가 속한 비공개 채널이 아닐 경우 참(continue 처리 되어서 responses에 추가되지 않음)
                if (!joinedPrivateChannelIds.contains(channel.getId())) {
                    continue;
                }
            }

            // 공개 채널 + 유저가 속한 비공개 채널은 응답 DTO 생성 후 반환 리스트에 추가
            responses.add(toResponse(channel));
        }

        return responses;
    }

    @Override
    public ChannelResponse updateChannel(PublicChannelUpdateRequest request) {
        // DTO 검증
        if (request == null || request.id() == null) {
            throw new RuntimeException("채널이 존재하지 않습니다.");
        }

        // 수정 대상 채널이 존재하는지 검증
        Channel channel = channelRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("채널이 존재하지 않습니다."));

        // 비공개 채널일 경우 수정 불가
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new RuntimeException("비공개 채널은 수정할 수 없습니다.");
        }

        // 수정 DTO 데이터 확인
        String newName = request.newName();
        String newDescription = request.newDescription();

        // 새로운 채널 이름이 blank면 예외
        if (newName != null && newName.isBlank()) {
            throw new RuntimeException("채널 이름이 필요합니다.");
        }

        // 새로운 채널 설명이 blank면 예외
        if (newDescription != null && newDescription.isBlank()) {
            throw new RuntimeException("채널 설명이 필요합니다.");
        }

        // null이 아니면 새로운 데이터 주입, null이면 기존 데이터 유지
        String resolvedName = (newName != null) ? newName : channel.getName();
        String resolvedDescription = (newDescription != null) ? newDescription : channel.getDescription();

        // 채널 정보 수정 및 저장
        channel.update(resolvedName, resolvedDescription);
        channelRepository.save(channel);

        // 응답 DTO 생성 및 반환
        return toResponse(channel);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        if (channelId == null) {
            throw new RuntimeException("채널이 존재하지 않습니다.");
        }

        // 삭제 대상 채널이 존재하는지 검증
        channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("채널이 존재하지 않습니다."));

        // 삭제 채널의 메시지 제거
        messageRepository.deleteAllByChannelId(channelId);
        // 삭제 채널의 ReadStatus 제거
        readStatusRepository.deleteAllByChannelId(channelId);
        // 채널 삭제
        channelRepository.delete(channelId);
    }

    private void validateCreatePrivateRequest(PrivateChannelCreateRequest request) {
        if (request == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }
        if (request.userIds() == null || request.userIds().isEmpty()) {
            throw new RuntimeException("참여 유저가 필요합니다.");
        }

        // 참여 유저 검증 및 중복 불가 처리
        Set<UUID> deduplication = new HashSet<>();
        for (UUID userId : request.userIds()) {
            if (userId == null) {
                throw new RuntimeException("참여 유저가 올바르지 않습니다.");
            }
            if (!deduplication.add(userId)) {
                throw new RuntimeException("참여 유저가 중복되었습니다.");
            }
        }
    }

    private void validateCreatePublicRequest(PublicChannelCreateRequest request) {
        if (request == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new RuntimeException("채널 이름이 필요합니다.");
        }
        if (request.description() != null && request.description().isBlank()) {
            throw new RuntimeException("채널 설명이 필요합니다.");
        }
    }

    private ChannelResponse toResponse(Channel channel) {
        // 채널의 가장 최근 메시지 시각 조회
        Instant latestMessageTime = messageRepository.findLatestMessageTimeByChannelId(channel.getId())
                .orElse(null);

        // 참여자 목록
        List<UUID> userIds = List.of();
        // 비공개 채널일 경우 참여자 목록 조회
        if (channel.getType() == ChannelType.PRIVATE) {
            userIds = readStatusRepository.findParticipantUserIdsByChannelId(channel.getId());
        }

        // 응답 DTO 생성 및 반환
        return ChannelResponse.from(channel, latestMessageTime, userIds);
    }
}

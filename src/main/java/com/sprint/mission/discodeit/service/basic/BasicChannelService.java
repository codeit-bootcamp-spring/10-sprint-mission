package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDTO;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateString;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    // 공개 채널 생성
    @Override
    public ChannelResponseDTO createPublicChannel(PublicChannelCreateRequestDTO publicChannelCreateRequestDTO) {
        // 1. 사용자 존재 여부 확인
        userRepository.findById(publicChannelCreateRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

        // 2. 공개 채널 생성
        Channel newChannel = new Channel(publicChannelCreateRequestDTO);
        channelRepository.save(newChannel);

        return toResponseDTO(newChannel);
    }

    // 비공개 채널 생성
    @Override
    public ChannelResponseDTO createPrivateChannel(PrivateChannelCreateRequestDTO privateChannelCreateRequestDTO) {
        // 1. 사용자 존재 여부 확인
        userRepository.findById(privateChannelCreateRequestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        // 2. 비공개 채널 생성
        Channel newChannel = new Channel(privateChannelCreateRequestDTO);
        channelRepository.save(newChannel);

        // 3. 비공개 채널 멤버마다 ReadStatus 생성
        newChannel.getMembers()
                .forEach(memberId -> {
                    ReadStatusCreateRequestDTO readStatusCreateRequestDTO = new ReadStatusCreateRequestDTO(memberId, newChannel.getId());
                    ReadStatus memeberReadStatus = new ReadStatus(readStatusCreateRequestDTO);
                    readStatusRepository.save(memeberReadStatus);
                });

        return toResponseDTO(newChannel);
    }

    // 채널 단건 조회
    @Override
    public ChannelResponseDTO findById(UUID targetChannelId) {
        // 1. 채널 존재 여부 확인
        Channel targetChannel = channelRepository.findById(targetChannelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        // 2. 채널 응답 DTO 생성 및 반환
        return toResponseDTO(targetChannel);
    }

    // 채널 전체 조회
    @Override
    public List<ChannelResponseDTO> findAll() {
        // 1. 전체 채널 목록 조회
        List<Channel> channels = channelRepository.findAll();

        // 2. 전체 채널 목록 응답 DTO 생성 및 반환
        return channelRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // 채널 전체 조회 (비공개 채널은 해당 사용자가 참여한 전체 채널)
    public List<ChannelResponseDTO> findAllByUserId(UUID userId) {
        // 1. 사용자 존재 여부 확인
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

        // 2. 해당 사용자가 참여한 전체 채널 목록 응답 DTO 생성 및 반환
        return channelRepository.findAll().stream()
                .filter(channel ->
                        // 공개 채널은 전체 채널 목록 반환
                        channel.getType() == ChannelType.PUBLIC ||
                        // 비공개 채널은 해다 유저가 참여한 채널 목록만 반환
                        channel.getType() == ChannelType.PRIVATE && channel.getMembers().contains(targetUser.getId()))
                .map(this::toResponseDTO)
                .toList();
    }

    // 채널 정보 수정
    @Override
    public ChannelResponseDTO updateChannel(ChannelUpdateRequestDTO channelUpdateRequestDTO) {
        // 1. 채널 존재 여부 확인
        Channel targetChannel = findEntityById(channelUpdateRequestDTO.getId());

        // 2. Private 채널 제외
        if (targetChannel.getType() == ChannelType.PRIVATE)
            throw new RuntimeException("Private 채널은 설정 및 정보를 변경할 수 없습니다.");

        // 3. 채널 이름 변경
        Optional.ofNullable(channelUpdateRequestDTO.getChannelName())
                .ifPresent(channelName -> {
                    validateString(channelName, "[채널 이름 변경 실패] 올바른 채널 이름 형식이 아닙니다.");
                    validateDuplicateValue(targetChannel.getChannelName(), channelName, "[채널 이름 변경 실패] 현재 채널 이름과 동일합니다.");
                    targetChannel.updateChannelName(channelUpdateRequestDTO.getChannelName());
                });

        // 채널 설명 변경
        Optional.ofNullable(channelUpdateRequestDTO.getChannelDescription())
                .ifPresent(channelDescription -> {
                    validateString(channelDescription, "[채널 설명 변경 실패] 올바른 채널 설명 형식이 아닙니다.");
                    validateDuplicateValue(targetChannel.getDescription(), channelDescription, "[채널 설명 변경 실패] 현재 채널 설명과 동일합니다.");
                    targetChannel.updateChannelDescription(channelUpdateRequestDTO.getChannelDescription());
                });

        // 3. 수정된 채널 저장 및 응답 DTO 생성
        channelRepository.save(targetChannel);
        return toResponseDTO(targetChannel);
    }

    // 채널 삭제
    @Override
    public void deleteChannel(UUID targetChannelId) {
        // 1. 채널 존재 유무 확인
        Channel targetChannel = findEntityById(targetChannelId);

        // 2. 메시지 연쇄 삭제
        messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(targetChannelId))
                .forEach(messageRepository::delete);

        // 3. 읽음 상태 연쇄 삭제
        readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(targetChannelId))
                .forEach(readStatusRepository::delete);

        // 4. 채널 삭제
        channelRepository.delete(targetChannel);
    }

    // 채널 참가자 초대
    @Override
    public void inviteMembers(UUID targetUserId, UUID targetChannelId) {
        User newUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
        Channel targetChannel = findEntityById(targetChannelId);

        validateMemberExists(targetUserId, targetChannelId);

        targetChannel.getMembers().add(newUser.getId());
        channelRepository.save(targetChannel);
    }

    // 채널 퇴장
    @Override
    public void leaveMembers(UUID targetUserId, UUID targetChannelId) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
        Channel targetChannel = findEntityById(targetChannelId);

        validateUserNotInChannel(targetUserId, targetChannelId);

        targetChannel.getMembers().removeIf(memberId -> memberId.equals(targetUserId));
        channelRepository.save(targetChannel);
    }

    // 유효성 검증 (초대)
    public void validateMemberExists(UUID userId, UUID channelId) {
        List<UUID> currentMembers = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."))
                .getMembers();

        if (currentMembers.stream().anyMatch(memberId -> memberId.equals(userId))) {
            throw new IllegalArgumentException("이미 채널에 존재하는 사용자입니다.");
        }
    }

    // 유효성 검증 (퇴장)
    public void validateUserNotInChannel(UUID userId, UUID channelId) {
        List<UUID> currentMembers = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."))
                .getMembers();

        if (currentMembers.stream().noneMatch(member -> member.equals(userId))) {
            throw new IllegalArgumentException("해당 채널에 존재하는 사용자가 아닙니다.");
        }
    }

    // 채널 엔티티 반환
    public Channel findEntityById(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));
    }

    // 가장 최근 메시지 시간 정보 조회
    public Instant getLastMessageAt(UUID targetChannelId) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(targetChannelId))
                .max(Comparator.comparing(Message::getCreatedAt))
                .map(Message::getCreatedAt)
                .orElse(null);
    }

    public ChannelResponseDTO toResponseDTO(Channel channel) {
        return ChannelResponseDTO.builder()
                .id(channel.getId())
                .userId(channel.getUserId())
                .channelName(channel.getChannelName())
                .members((channel.getType() == ChannelType.PRIVATE)? channel.getMembers() : List.of())
                .channelType(channel.getType())
                .description(channel.getDescription())
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .lastMessageAt(getLastMessageAt(channel.getId()))
                .build();
    }
}
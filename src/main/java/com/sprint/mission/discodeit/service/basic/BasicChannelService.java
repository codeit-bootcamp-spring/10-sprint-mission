package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.channel.ChannelMemberRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.ChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDTO;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        userRepository.findById(publicChannelCreateRequestDTO.userId())
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

        ChannelEntity newChannel = new ChannelEntity(publicChannelCreateRequestDTO);
        channelRepository.save(newChannel);

        return toResponseDTO(newChannel);
    }

    // 비공개 채널 생성
    @Override
    public ChannelResponseDTO createPrivateChannel(PrivateChannelCreateRequestDTO privateChannelCreateRequestDTO) {
        userRepository.findById(privateChannelCreateRequestDTO.userId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        ChannelEntity newChannel = new ChannelEntity(privateChannelCreateRequestDTO);
        channelRepository.save(newChannel);

        newChannel.getMembers()
                .forEach(memberId -> {
                    // 채널한 속한 멤버마다 ReadStatus 생성
                    ReadStatusCreateRequestDTO readStatusCreateRequestDTO = new ReadStatusCreateRequestDTO(memberId, newChannel.getId());
                    ReadStatusEntity memeberReadStatus = new ReadStatusEntity(readStatusCreateRequestDTO);
                    readStatusRepository.save(memeberReadStatus);
                });

        return toResponseDTO(newChannel);
    }

    // 채널 단건 조회
    @Override
    public ChannelResponseDTO findById(UUID targetChannelId) {
        ChannelEntity targetChannel = channelRepository.findById(targetChannelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        return toResponseDTO(targetChannel);
    }

    // 채널 전체 조회
    @Override
    public List<ChannelResponseDTO> findAll() {
        return channelRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // 채널 전체 조회 (비공개 채널은 해당 사용자가 참여한 전체 채널)
    public List<ChannelResponseDTO> findAllByUserId(UUID userId) {
        UserEntity targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

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
    public ChannelResponseDTO update(UUID channelId, ChannelUpdateRequestDTO channelUpdateRequestDTO) {
        ChannelEntity targetChannel = findEntityById(channelId);

        // Private 채널 제외
        if (targetChannel.getType() == ChannelType.PRIVATE)
            throw new RuntimeException("Private 채널은 설정 및 정보를 변경할 수 없습니다.");

        // 채널 이름 변경
        Optional.ofNullable(channelUpdateRequestDTO.channelName())
                .ifPresent(channelName -> {
                    validateString(channelName, "[채널 이름 변경 실패] 올바른 채널 이름 형식이 아닙니다.");
                    validateDuplicateValue(targetChannel.getChannelName(), channelName, "[채널 이름 변경 실패] 현재 채널 이름과 동일합니다.");
                    targetChannel.updateChannelName(channelUpdateRequestDTO.channelName());
                });

        // 채널 설명 변경
        Optional.ofNullable(channelUpdateRequestDTO.description())
                .ifPresent(channelDescription -> {
                    validateString(channelDescription, "[채널 설명 변경 실패] 올바른 채널 설명 형식이 아닙니다.");
                    validateDuplicateValue(targetChannel.getDescription(), channelDescription, "[채널 설명 변경 실패] 현재 채널 설명과 동일합니다.");
                    targetChannel.updateChannelDescription(channelUpdateRequestDTO.description());
                });

        channelRepository.save(targetChannel);
        return toResponseDTO(targetChannel);
    }

    // 채널 삭제
    @Override
    public void delete(UUID targetChannelId) {
        ChannelEntity targetChannel = findEntityById(targetChannelId);

        // 해당 채널에서 발행된 메시지 연쇄 삭제
        List<MessageEntity> deleteMessages = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(targetChannelId))
                .toList();
        deleteMessages.forEach(messageRepository::delete);

        // 채널 멤버의 ReadStatus 연쇄 삭제
        List<ReadStatusEntity> deleteReadStatuses = readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(targetChannelId))
                .toList();
        deleteReadStatuses.forEach(readStatusRepository::delete);

        channelRepository.delete(targetChannel);
    }

    // 채널 참가자 초대
    @Override
    public void inviteMember(ChannelMemberRequestDTO channelMemberRequestDTO) {
        UserEntity newUser = userRepository.findById(channelMemberRequestDTO.userId())
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
        ChannelEntity targetChannel = findEntityById(channelMemberRequestDTO.channelId());

        validateMemberExists(channelMemberRequestDTO.userId(), channelMemberRequestDTO.channelId());

        targetChannel.getMembers().add(newUser.getId());
        channelRepository.save(targetChannel);
    }

    // 채널 퇴장
    @Override
    public void leaveMember(ChannelMemberRequestDTO channelMemberRequestDTO) {
        UserEntity targetUser = userRepository.findById(channelMemberRequestDTO.userId())
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
        ChannelEntity targetChannel = findEntityById(channelMemberRequestDTO.channelId());

        validateUserNotInChannel(channelMemberRequestDTO.userId(), channelMemberRequestDTO.channelId());

        targetChannel.getMembers().removeIf(memberId -> memberId.equals(targetUser.getId()));
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
    public ChannelEntity findEntityById(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));
    }

    // 응답 DTO 생성 및 반환
    public ChannelResponseDTO toResponseDTO(ChannelEntity channel) {
        return ChannelResponseDTO.builder()
                .id(channel.getId())
                .userId(channel.getUserId())
                .channelName(channel.getChannelName())
                // 비공개 채널일 때만 member 리스트 반환
                .members((channel.getType() == ChannelType.PRIVATE)? channel.getMembers() : List.of())
                .channelType(channel.getType())
                .description(channel.getDescription())
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .lastMessageAt(messageRepository.getLastMessageAt(channel.getId()))
                .build();
    }
}
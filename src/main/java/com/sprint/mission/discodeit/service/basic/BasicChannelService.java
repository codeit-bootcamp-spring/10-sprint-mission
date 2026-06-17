package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.channel.ChannelMemberRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.*;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    private final ChannelMapper channelMapper;

    // 공개 채널 생성
    @Override
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @CacheEvict(cacheNames = "channels", key = "#userId")
    @Transactional
    public ChannelDto createPublicChannel(PublicChannelCreateRequest publicChannelCreateRequest) {
        ChannelEntity newChannel = channelMapper.toPublicEntity(publicChannelCreateRequest);
        channelRepository.save(newChannel);

        log.info("[PUBLIC_CHANNEL_CREATE] 공개 채널 생성 완료: id={}", newChannel.getId());
        return toChannelDto(newChannel);
    }

    // 비공개 채널 생성
    @Override
    @CacheEvict(cacheNames = "channels", key = "#userId")
    @Transactional
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest privateChannelCreateRequest) {
        ChannelEntity newChannel = channelMapper.toPrivateEntity();

        // 비공개 채널에 초대하고자 하는 사용자 조회
        List<UserEntity> members = userRepository.findAllById(privateChannelCreateRequest.participantIds());

        if (members.size() != privateChannelCreateRequest.participantIds().size()) {
            throw new UserNotFoundException();
        }

        members.forEach(member -> {
            // 각 멤버의 읽음 상태 생성
            ReadStatusEntity memberReadStatus = new ReadStatusEntity(member, newChannel);
            newChannel.getReadStatuses().add(memberReadStatus);
        });

        channelRepository.save(newChannel);
        log.info("[PRIVATE_CHANNEL_CREATE] 비공개 채널 생성 완료: id={}, 총 {}명",
                newChannel.getId(),
                newChannel.getReadStatuses().size()
        );
        return toChannelDto(newChannel);
    }

    // 채널 단건 조회
    @Override
    public ChannelDto findById(UUID channelId) {
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelId);

        return toChannelDto(targetChannel);
    }

    // 채널 전체 조회
    @Override
    public List<ChannelDto> findAll() {
        return channelRepository.findAll().stream()
                .map(this::toChannelDto)
                .toList();
    }

    // 특정 사용자가 참여하고 있는 채널 목록 조회
    @Cacheable(cacheNames = "channels", key = "#userId")
    public List<ChannelDto> findAllByUserId(UUID userId) {
        UserEntity targetUser = getUserEntityOrThrow(userId);

        return channelRepository.findAllVisibleChannelByUserId(targetUser.getId()).stream()
                .map(this::toChannelDto)
                .toList();
    }

    // 채널 정보 수정
    @Override
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @CacheEvict(cacheNames = "channels", key = "#userId")
    @Transactional
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest) {
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelId);

        // Private 채널 제외
        if (targetChannel.getType() == ChannelType.PRIVATE) {
            throw new PrivateChannelNotUpdatableException();
        }

        // 채널 이름 및 설명 덮어쓰기
        targetChannel.updateChannelName(publicChannelUpdateRequest.newName());
        targetChannel.updateChannelDescription(publicChannelUpdateRequest.newDescription());

        log.info("[PUBLIC_CHANNEL_UPDATE] 공개 채널 수정 완료: id={}", targetChannel.getId());
        return toChannelDto(targetChannel);
    }

    // 채널 삭제
    @Override
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @CacheEvict(cacheNames = "channels", key = "#userId")
    @Transactional
    public void delete(UUID channelId) {
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelId);

        channelRepository.delete(targetChannel);
        log.info("[CHANNEL_DELETE] 채널 삭제 완료: id={}", targetChannel.getId());
    }

    // 채널 참가자 초대
    @Override
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    public void inviteMember(ChannelMemberRequestDTO channelMemberRequestDTO) {
        UserEntity newUser = getUserEntityOrThrow(channelMemberRequestDTO.userId());
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelMemberRequestDTO.channelId());

        validateMemberExists(newUser.getId(), targetChannel.getId());

        ReadStatusEntity newMemberReadStatus = new ReadStatusEntity(newUser, targetChannel);
        targetChannel.getReadStatuses().add(newMemberReadStatus);
    }

    // 채널 퇴장
    @Override
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    public void leaveMember(ChannelMemberRequestDTO channelMemberRequestDTO) {
        UserEntity targetUser = getUserEntityOrThrow(channelMemberRequestDTO.userId());
        ChannelEntity targetChannel = getChannelEntityOrThrow(channelMemberRequestDTO.channelId());

        validateUserNotInChannel(targetUser.getId(), targetChannel.getId());

        ReadStatusEntity targetReadStatus = getReadStatusEntityOrThrow(targetUser.getId(), targetChannel.getId());
        targetChannel.getReadStatuses().remove(targetReadStatus);
    }

    // 사용자 반환
    private UserEntity getUserEntityOrThrow(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    // 채널 반환
    private ChannelEntity getChannelEntityOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));
    }

    // 읽음 상태 반환
    private ReadStatusEntity getReadStatusEntityOrThrow(UUID userId, UUID channelId) {
        return readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() -> new ReadStatusNotFoundException(userId, channelId));
    }

    // 유효성 검증 (읽음 상태 존재 여부)
    private boolean existsReadStatusByUserIdAndChannelId(UUID userId, UUID channelId) {
        return readStatusRepository.existsByUserIdAndChannelId(userId, channelId);
    }

    // 유효성 검증 (초대)
    private void validateMemberExists(UUID userId, UUID channelId) {
        if (existsReadStatusByUserIdAndChannelId(userId, channelId)) {
            throw new ChannelParticipantAlreadyExistsException(userId, channelId);
        }
    }

    // 유효성 검증 (퇴장)
    private void validateUserNotInChannel(UUID userId, UUID channelId) {
        if (!existsReadStatusByUserIdAndChannelId(userId, channelId)) {
            throw new ChannelParticipantNotFoundException(userId, channelId);
        }
    }

    // DTO 변환
    private ChannelDto toChannelDto(ChannelEntity channel) {
        // 비공개 채널일 경우에만 참여자 목록 반환
        List<UserEntity> participants = List.of();

        if (channel.getType() == ChannelType.PRIVATE) {
            participants = channel.getReadStatuses().stream()
                    .map(ReadStatusEntity::getUser)
                    .toList();
        }

        // 해당 채널에서 마지막으로 발행된 메시지 시간
        Instant lastMessageAt = messageRepository.getLastMessageAt(channel.getId());

        return channelMapper.toDto(channel, participants, lastMessageAt);
    }
}
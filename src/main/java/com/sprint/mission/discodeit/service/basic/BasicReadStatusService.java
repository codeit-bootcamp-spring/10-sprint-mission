package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.ReadStatusType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;

    // 읽음 상태 생성
    @Override
    public ReadStatusResponseDTO create(ReadStatusCreateRequestDTO readStatusCreateRequestDTO) {
        User targetUser = userRepository.findById(readStatusCreateRequestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        Channel targetChannel = channelRepository.findById(readStatusCreateRequestDTO.getChannelId())
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        existsByUserIdAndChannelId(targetUser.getId(), targetChannel.getId());

        ReadStatus newReadStatus = new ReadStatus(readStatusCreateRequestDTO);
        readStatusRepository.save(newReadStatus);

        return toResponseDTO(newReadStatus);
    }

    // 읽음 상태 단건 조회
    @Override
    public ReadStatusResponseDTO findById(UUID id) {
        ReadStatus targetReadStatus = findEntityById(id);

        return toResponseDTO(targetReadStatus);
    }

    // 읽음 상태 전체 조회
    @Override
    public List<ReadStatusResponseDTO> findAll() {
        return readStatusRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // 특정 사용자의 읽음 상태 조회
    @Override
    public ReadStatusResponseDTO findAllByReadStatusId(UUID userId) {
        return readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .findFirst()
                .map(this::toResponseDTO)
                .orElse(null);
    }

    // 읽음 상태 수정
    @Override
    public ReadStatusResponseDTO update(ReadStatusUpdateRequestDTO readStatusUpdateRequestDTO) {
        ReadStatus targetReadStatus = findEntityById(readStatusUpdateRequestDTO.getId());

        if (readStatusUpdateRequestDTO.getReadStatusType() != null) {
            targetReadStatus.updateReadStatusType(readStatusUpdateRequestDTO.getReadStatusType());
        }
        else {
            targetReadStatus.updateReadStatusType(ReadStatusType.READ);
        }

        targetReadStatus.updateLastReadTime();
        readStatusRepository.save(targetReadStatus);

        return toResponseDTO(targetReadStatus);
    }

    // 읽음 상태 삭제
    @Override
    public void delete(UUID id) {
        ReadStatus targetReadStatus = findEntityById(id);
        readStatusRepository.delete(targetReadStatus);
    }

    // 유효성 검사 (중복 확인)
    public void existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        if (readStatusRepository.existsByUserIdAndChannelId(userId, channelId)) {
            throw new RuntimeException("관련된 상태 정보가 이미 존재합니다.");
        }
    }

    // 단일 엔티티 조회 및 반환
    public ReadStatus findEntityById(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
    }

    // 응답 DTO 생성 및 반환
    public ReadStatusResponseDTO toResponseDTO(ReadStatus readStatus) {
        return ReadStatusResponseDTO.builder()
                .id(readStatus.getId())
                .userId(readStatus.getUserId())
                .channelId(readStatus.getChannelId())
                .createdAt(readStatus.getCreatedAt())
                .updatedAt(readStatus.getUpdatedAt())
                .lastReadTime(readStatus.getLastReadTime())
                .build();
    }
}
package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    // 읽음 상태 생성
    ReadStatusResponseDTO create(ReadStatusCreateRequestDTO readStatusCreateRequestDTO);

    // 읽음 상태 단일 조회
    ReadStatusResponseDTO findById(UUID id);

    // 읽음 상태 전체 조회
    List<ReadStatusResponseDTO> findAll();

    // 읽음 상태 수정
    ReadStatusResponseDTO update(ReadStatusUpdateRequestDTO readStatusUpdateRequestDTO);

    // 읽음 상태 삭제
    void delete(UUID id);
}

package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDTO;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    // 사용자 상태 생성
    UserStatusResponseDTO create(UserStatusCreateRequestDTO userStatusCreateRequestDTO);

    // 사용자 상태 단일 조회
    UserStatusResponseDTO findById(UUID id);

    // 사용자 상태 전체 조회
    List<UserStatusResponseDTO> findAll();

    // 사용자 상태 수정
    UserStatusResponseDTO updateByUserId(UserStatusUpdateRequestDTO userStatusUpdateRequestDTO);

    // 사용자 상태 삭제
    UserStatusResponseDTO delete(UUID id);
}

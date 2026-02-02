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
    UserStatusResponseDTO findById(UUID targetUserStatusId);

    // 사용자 상태 전체 조회
    List<UserStatusResponseDTO> findAll();

    // 사용자 상태 수정
    UserStatusResponseDTO update(UserStatusUpdateRequestDTO userStatusUpdateRequestDTO);

    // 특정 사용자의 상태 수정
    UserStatusResponseDTO updateByUserId(UUID targetUserId);

    // 사용자 상태 삭제
    void delete(UUID id);
}

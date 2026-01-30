package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.MemberFindRequestDTO;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.UserResponseDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 사용자 생성
    UserResponseDTO createUser(UserCreateRequestDTO userCreateRequestDTO);

    // 사용자 단건 조회
    UserResponseDTO findById(UUID userId);

    // 사용자 전체 조회
    List<UserResponseDTO> findAll();

    // 채널 내 멤버 목록 조회
    List<UserResponseDTO> findMembersByChannelId(MemberFindRequestDTO memberFindRequestDTO);

    // 사용자 수정
    UserResponseDTO updateUser(UserUpdateRequestDTO userUpdateRequestDTO);

    // 사용자 삭제
    void deleteUser(UUID userId);
}

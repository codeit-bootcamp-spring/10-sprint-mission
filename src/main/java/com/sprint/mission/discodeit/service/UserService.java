package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.UserCreateResponseDTO;
import com.sprint.mission.discodeit.dto.response.UserSearchResponseDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatusType;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 사용자 생성
    UserCreateResponseDTO createUser(UserCreateRequestDTO userCreateRequestDTO, BinaryContentCreateRequestDTO binaryContentCreateRequestDTO);

    // 사용자 단건 조회
    UserSearchResponseDTO searchUser(UUID userId);

    // 사용자 전체 조회
    List<UserSearchResponseDTO> searchUserAll();

    // 채널 내 멤버 목록 조회
    List<UUID> searchMembersByChannelId(UUID channelId);

    // 사용자 수정
    User updateUser(UUID userId, String newPassword, String newNickname, UserStatusType newUserStatus);

    // 사용자 저장
    void updateUser(UUID userId, User user);

    // 사용자 삭제
    void deleteUser(UUID userId);
}

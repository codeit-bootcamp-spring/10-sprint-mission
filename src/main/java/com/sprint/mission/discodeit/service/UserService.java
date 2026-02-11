package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.UserDetailResponseDTO;
import com.sprint.mission.discodeit.dto.response.UserSummaryResponseDTO;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
//    User create(String username, String email, String password);
    UserSummaryResponseDTO create(UserCreateRequestDTO userCreateRequestDTO);
//    User find(UUID userId);
    UserDetailResponseDTO find(UUID userId);
//    List<User> findAll();
    List<UserDetailResponseDTO> findAll();
    UserSummaryResponseDTO update(UUID userId, UserUpdateRequestDTO userUpdateRequestDTO);
    void delete(UUID userId);
}

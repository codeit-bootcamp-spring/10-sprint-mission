package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userdto.UserRegitrationRecord;
import com.sprint.mission.discodeit.dto.userdto.UserResponseDTO;
import com.sprint.mission.discodeit.dto.userdto.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserRegitrationRecord req);
    UserResponseDTO find(UUID userId);
    List<UserResponseDTO> findAll();
    User update(UserUpdateDTO userUpdateDTO);
    void delete(UUID userId);
}

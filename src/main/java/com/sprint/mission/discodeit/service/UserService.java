package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.UserDTO.UserRegitrationRecord;
import com.sprint.mission.discodeit.DTO.UserDTO.UserReturnDTO;
import com.sprint.mission.discodeit.DTO.UserDTO.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserRegitrationRecord req);
    UserReturnDTO find(UUID userId);
    List<UserReturnDTO> findAll();
    User update(UserUpdateDTO userUpdateDTO);
    void delete(UUID userId);
}

package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.BinaryContentRecord;
import com.sprint.mission.discodeit.DTO.UserRegitrationRecord;
import com.sprint.mission.discodeit.DTO.UserReturnDTO;
import com.sprint.mission.discodeit.DTO.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(UserRegitrationRecord req);
    UserReturnDTO find(UUID userId);
    List<UserReturnDTO> findAll();
    User update(UserUpdateDTO userUpdateDTO);
    void delete(UUID userId);
}

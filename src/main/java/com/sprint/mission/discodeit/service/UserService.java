package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDTO.Response create(UserDTO.Create createRequest);
    UserDTO.Response findById(UUID userId);
    List<UserDTO.Response> findAll();
    UserDTO.Response update(UserDTO.Update updateRequest);
    void delete(UUID userId);
}

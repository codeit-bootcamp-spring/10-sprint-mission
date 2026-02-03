package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.*;

import java.util.*;

public interface UserService {

    UserDTO create(UserCreateDTO userCreateDTO);

    UserDTO findById(UUID id);

    List<UserDTO> findAll();

    UserDTO updateUser(UserUpdateDTO userUpdateDTO);

    void delete(UUID userId);
}
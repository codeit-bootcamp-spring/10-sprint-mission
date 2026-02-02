package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.*;

import java.util.*;

public interface UserService {

    UserDTO create(UserCreateDTO userCreateDTO);

    UserFindDTO find(UUID id);

    UserFindAllDTO findAll();

    UserDTO updateUser(UserUpdateDTO userUpdateDTO);

    void delete(UUID id);
}
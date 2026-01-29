package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.DTO.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.DTO.UserCreateDTO;
import com.sprint.mission.discodeit.entity.DTO.UserFindAllDTO;
import com.sprint.mission.discodeit.entity.DTO.UserFindDTO;

import java.util.*;

public interface UserService {

    User create(UserCreateDTO userCreateDTO);

    UserFindDTO find(UUID id);

    UserFindAllDTO findAll();

    User updateUser(UserUpdateDTO userUpdateDTO);

    void delete(UUID id);
}
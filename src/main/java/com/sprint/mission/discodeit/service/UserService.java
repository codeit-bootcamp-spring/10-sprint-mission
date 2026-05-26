package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto create(UserCreateRequest request, MultipartFile profile);
    UserDto findUser(UUID userId);
    List<UserDto> findAllUsers();
    UserDto update(UUID userId, UserUpdateRequest request, MultipartFile profile);
    void delete(UUID userId);
}

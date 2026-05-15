package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  UserDto create(UserCreateRequest createRequest, MultipartFile file);

  UserDto findById(UUID userId);

  List<UserDto> findAll();

  UserDto update(UUID userId, UserUpdateRequest updateRequest, MultipartFile file);

  UserDto updateRole(RoleUpdateRequest request);

  void delete(UUID userId);
}

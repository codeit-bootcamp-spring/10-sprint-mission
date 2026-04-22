package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.FileUploadDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

  UserDto create(UserCreateRequest request, Optional<FileUploadDto> profile);

  UserDto find(UUID id);

  List<UserDto> findAll();

  UserDto update(UUID id, UserUpdateRequest request, Optional<FileUploadDto> profile);

  void delete(UUID id);
}

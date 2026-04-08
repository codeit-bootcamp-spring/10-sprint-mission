package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  UserDto create(UserCreateRequest request, MultipartFile profile);

  UserDto find(UUID id);

  List<UserDto> findAll();

  UserDto update(UUID id, UserUpdateRequest request, MultipartFile profile);

  void delete(UUID id);
}

package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

import java.util.UUID;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {

  com.sprint.mission.discodeit.dto.user.User create(UserCreateRequest request,
      MultipartFile profileFile);

  com.sprint.mission.discodeit.dto.user.User findById(UUID id);

  List<UserDto> findAll();

  com.sprint.mission.discodeit.dto.user.User update(UUID id, UserUpdateRequest request,
      MultipartFile profileFile);

  void deleteById(UUID id);
}

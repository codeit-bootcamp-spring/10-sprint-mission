package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  UserDto create(UserCreateRequest request, MultipartFile profile);

  UserDto findById(UUID id);

  UserDto findByEmail(String email);

  List<UserDto> findAllUsers();

  List<UserDto> findAllByChannelId(UUID channelId);

  @PreAuthorize("#root.args[0] == authentication.principal.userDto.id")
  UserDto update(UUID userId, UserUpdateRequest request, MultipartFile profile);

  @PreAuthorize("hasRole('ADMIN')")
  UserDto updateUserRole(UserRoleUpdateRequest request);

  @PreAuthorize("#root.args[0] == authentication.principal.userDto.id")
  void delete(UUID id);
}

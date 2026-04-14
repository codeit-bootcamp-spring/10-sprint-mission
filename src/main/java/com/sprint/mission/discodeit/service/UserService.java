package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {

  User create(String username, String email, String password, MultipartFile profileFile);

  User findById(UUID id);

  List<User> findAll();

  User update(UUID id, String newUsername, String newEmail, String newPassword,
      MultipartFile profileFile);

  void deleteById(UUID id);
}

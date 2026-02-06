package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto.Response create(UserDto.CreateRequest request, MultipartFile file);
    UserDto.Response find(UUID userId);
    List<UserDto.Response> findAll();
    UserDto.Response update(UUID userId, UserDto.UpdateRequest request, MultipartFile file);
    void delete(UUID userId);
    UserDto.Response toDto(User user);
}

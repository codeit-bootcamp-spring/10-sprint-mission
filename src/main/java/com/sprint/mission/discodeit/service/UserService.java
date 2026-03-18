package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto createUser(UserDto.UserCreateRequest userReq, MultipartFile profileImage) throws IOException;
    List<UserDto> findAllUsers();
    UserDto updateUser(UUID uuid, UserDto.UserUpdateRequest userReq, MultipartFile profileImage) throws IOException;
    void deleteUser(UUID uuid);
}

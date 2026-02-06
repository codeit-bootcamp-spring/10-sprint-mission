package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(UserCreateRequestDto userCreateRequestDto, MultipartFile profileImageFile) throws IOException;
    UserResponseDto find(UUID userId);
    List<UserResponseDto> findAll();
    UserResponseDto update(UserUpdateRequestDto userUpdateRequestDto, MultipartFile profileImageFile) throws IOException;
    void delete(UUID userId);
}

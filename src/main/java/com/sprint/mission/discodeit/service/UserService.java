package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserRequestCreateDto;
import com.sprint.mission.discodeit.dto.user.UserRequestUpdateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(UserRequestCreateDto request, MultipartFile profileImage);

    UserResponseDto find(UUID id);

    List<UserResponseDto> findAll();

    UserResponseDto update(UserRequestUpdateDto request, MultipartFile profileImage);

    void delete(UUID id);

    List<UserResponseDto> findUsersByChannel(UUID channelId);



}

package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserApi {

    ResponseEntity<UserDto> create(UserCreateRequest userCreateRequest, MultipartFile profile);

    ResponseEntity<UserDto> update(UUID userId, UserUpdateRequest userUpdateRequest, MultipartFile profile);

    ResponseEntity<Void> delete(UUID userId);

    ResponseEntity<List<UserDto>> findAll();

    ResponseEntity<UserStatusDto> updateUserStatusByUserId(UUID userId, UserStatusUpdateRequest request);
}
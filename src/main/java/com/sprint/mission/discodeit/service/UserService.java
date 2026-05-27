package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    // Create
    UserDto create(UserCreateRequest request, MultipartFile file) throws IOException;

    // Read
    UserDto findById(UUID id);

    // ReadAll
    List<UserDto> findAll();

    // Update
    @PreAuthorize("#id == authentication.principal.userDto.id")
    UserDto update(UUID id, UserUpdateRequest request, MultipartFile file) throws IOException;

    // Delete
    @PreAuthorize("#id == authentication.principal.userDto.id")
    void delete(UUID id);

    @PreAuthorize("hasRole('ADMIN')")
    UserDto updateRole(UserRoleUpdateRequest request);

}

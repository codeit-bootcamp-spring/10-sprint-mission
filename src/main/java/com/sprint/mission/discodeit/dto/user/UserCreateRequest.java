package com.sprint.mission.discodeit.dto.user;


import java.util.Optional;

public record UserCreateRequest (
        String userName,
        String email,
        String password,
        ProfileImageCreateRequest profileImage
) {}

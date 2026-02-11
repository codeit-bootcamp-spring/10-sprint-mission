package com.sprint.mission.discodeit.dto.user;


public record UserCreateRequest (
        String userName,
        String email,
        String password,
        ProfileImageCreateRequest profileImage
) {}

package com.sprint.mission.discodeit.dto.user;


import java.util.Optional;

public record UserCreateRequest (
        String userName,
        String email,
        String password,
        Optional<ProfileImageCreateRequest> profileImage
) {}

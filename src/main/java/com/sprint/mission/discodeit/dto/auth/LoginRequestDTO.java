package com.sprint.mission.discodeit.dto.auth;

public record LoginRequestDTO(
        String email,
        String password
) { }
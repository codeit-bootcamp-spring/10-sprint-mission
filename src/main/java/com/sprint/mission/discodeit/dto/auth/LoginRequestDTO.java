package com.sprint.mission.discodeit.dto.auth;

public record LoginRequestDTO(
        String username,
        String password
) { }
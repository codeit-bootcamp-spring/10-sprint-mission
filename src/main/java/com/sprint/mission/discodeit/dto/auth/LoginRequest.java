package com.sprint.mission.discodeit.dto.auth;

// 로그인 요청 DTO
public record LoginRequest (
        String username,
        String password
) {}

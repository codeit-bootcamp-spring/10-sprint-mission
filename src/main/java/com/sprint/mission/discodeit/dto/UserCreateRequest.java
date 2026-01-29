package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.repository.BinaryContentRepository;

//유저 생성 용 DTO
public record UserCreateRequest(
        String userName,
        String alias,
        String email,
        String password,
        BinaryContentCreateRequest profileImage
) {
}

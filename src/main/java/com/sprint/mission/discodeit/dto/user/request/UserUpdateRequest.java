package com.sprint.mission.discodeit.dto.user.request;

import java.util.UUID;

// update할 때 id를 받는 것이 아닌 이미지 자체를 받는 다고 가정
public record UserUpdateRequest(
        UUID userID,
        String name,
        String type,
        byte[] profileImage
) { }

package com.sprint.mission.discodeit.dto.user.request;

import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;

// update할 때 id를 받는 것이 아닌 이미지 자체를 받는 다고 가정
public record UserUpdateRequest(
        String newUserName,
        String newEmail,
        BinaryContentCreateRequest profileImage
) { }

package com.sprint.mission.discodeit.dto.user.request;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public record UserFindRequest(
    UUID userID,
    String name,
    String email,
    boolean status,
    BinaryContent binaryContent
) {
}

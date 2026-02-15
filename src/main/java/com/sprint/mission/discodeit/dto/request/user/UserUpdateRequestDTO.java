package com.sprint.mission.discodeit.dto.request.user;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusUpdateRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserUpdateRequestDTO (
    @NotBlank
    String password,

    @NotBlank
    String username,

    @Valid
    UserStatusUpdateRequestDTO userStatusCreateRequestDTO,

    @Valid
    BinaryContentCreateRequestDTO binaryContentCreateRequestDTO
) {

}

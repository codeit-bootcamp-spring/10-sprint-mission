package com.sprint.mission.discodeit.dto.request.user;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusUpdateRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDTO {
    @Setter
    @NotNull
    private UUID id;

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;

    @Valid
    private UserStatusUpdateRequestDTO userStatusCreateRequestDTO;

    @Valid
    private BinaryContentCreateRequestDTO binaryContentCreateRequestDTO;
}

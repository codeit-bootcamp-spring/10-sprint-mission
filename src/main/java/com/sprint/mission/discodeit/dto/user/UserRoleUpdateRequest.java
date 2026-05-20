package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.Role;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

// 관리자가 사용자의 권한을 변경하기 위한 요청 dto
public record UserRoleUpdateRequest(

    @NotNull(message = "대상 사용자 ID를 입력하세요.")
    UUID userId,

    @NotNull(message = "변경할 권한을 입력하세요.")
    Role newRole
) {

}

package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 권한 수정 요청")
public class UserRoleUpdateRequest {

  @Schema(description = "권한을 수정할 사용자 ID", example = "550e8400-e29b-41d4-a716-446655440000")
  private UUID userId;

  @Schema(description = "변경할 권한", example = "ADMIN")
  private Role newRole;
}

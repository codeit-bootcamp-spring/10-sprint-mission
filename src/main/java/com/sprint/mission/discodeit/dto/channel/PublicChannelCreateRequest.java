package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.global.annotation.annotation.NotSpace;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Public Channel 생성 정보")
public record PublicChannelCreateRequest(
    @Size(max = 100)
    @NotBlank
    String name,

    @Size(max = 500)
    String description
) {

  public PublicChannelCreateRequest {
    name = name == null ? null : name.trim();
    description = description == null ? null : description.trim();
  }
}

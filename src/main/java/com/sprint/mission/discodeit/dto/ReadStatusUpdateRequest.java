package com.sprint.mission.discodeit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "읽음 상태 수정 요청")
public class ReadStatusUpdateRequest {

  @JsonProperty("newNotificationEnabled")
  @Schema(description = "채널 알림 활성화 여부", example = "true")
  private Boolean notificationEnabled;
}

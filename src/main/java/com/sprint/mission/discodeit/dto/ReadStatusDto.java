package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ReadStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public class ReadStatusDto {

  @Schema(name = "readStatusCreateRequest", description = "수신 정보 생성 요청")
  public record Create(
      @Schema(description = "유저 ID", example = "5cd294e0-4cde-4a67-8d5c-3f054927c595")
      UUID userId,
      @Schema(description = "채널 ID", example = "0ad06ce5-bdfb-4304-b6eb-a133a4b49fb8")
      UUID channelId
  ) {

  }

  @Schema(name = "readStatusResponse", description = "수신 정보 응답")
  public record Response(
      @Schema(description = "수신 정보 ID", example = "0d56555c-7d86-4fa7-b5d6-3170a70909e1")
      UUID id,
      @Schema(description = "유저 ID", example = "5cd294e0-4cde-4a67-8d5c-3f054927c595")
      UUID userId,
      @Schema(description = "채널 ID", example = "0ad06ce5-bdfb-4304-b6eb-a133a4b49fb8")
      UUID channelId,
      @Schema(description = "마지막 수신 시각", example = "2026-02-23T01:30:54Z")
      Instant lastReadAt
  ) {

    public static Response of(ReadStatus status) {
      return new Response(
          status.getId(),
          status.getUserId(),
          status.getChannelId(),
          status.getLastReadAt()
      );
    }
  }

  @Schema(name = "readStatusUpdateRequest", description = "수신 정보 수정 요청")
  public record Update(
      @Schema(description = "새로운 수신 시각", example = "2026-02-23T01:30:54Z")
      Instant newLastReadAt
  ) {

  }
}


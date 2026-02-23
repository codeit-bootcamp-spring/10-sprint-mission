package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChannelDto {

  @Schema(name = "publicChannelCreateRequest", description = "Public 채널 생성 요청")
  public record CreatePublic(
      @Schema(description = "채널 이름", example = "게임 채널")
      String name,
      @Schema(description = "채널 설명", example = "게임 채널입니다.")
      String description
  ) {

  }

  @Schema(name = "privateChannelCreateRequest", description = "Private 채널 생성 요청")
  public record CreatePrivate(
      @Schema(description = "채널 참여자 ID 목록", example = "[5cd294e0-4cde-4a67-8d5c-3f054927c595, 554fb7b8-8541-4310-8a03-693b1ffc13cf]")
      List<UUID> participantIds
  ) {

  }

  @Schema(name = "channelResponse", description = "채널 응답")
  public record Response(
      @Schema(description = "채널 ID", example = "0ad06ce5-bdfb-4304-b6eb-a133a4b49fb8")
      UUID id,
      @Schema(description = "채널 타입", example = "PUBLIC")
      ChannelType type,
      @Schema(description = "채널 이름", example = "게임 채널")
      String name,
      @Schema(description = "채널 설명", example = "게임 채널입니다.")
      String description,
      @Schema(description = "채널 참여자 ID 목록", example = "[5cd294e0-4cde-4a67-8d5c-3f054927c595, 554fb7b8-8541-4310-8a03-693b1ffc13cf]")
      List<UUID> participantIds,
      @Schema(description = "마지막 메시지 시각", example = "2026-02-23T01:30:54Z")
      Instant lastMessageAt
  ) {

    public static Response of(Channel channel, List<UUID> allUserIds, Instant lastMessageAt) {
      return new Response(
          channel.getId(),
          channel.getType(),
          channel.getName(),
          channel.getDescription(),
          allUserIds,
          lastMessageAt
      );
    }
  }

  @Schema(name = "publicChannelUpdateRequest", description = "채널 수정 요청")
  public record Update(
      @Schema(description = "새로운 채널 이름", example = "공부 채널")
      String newName,
      @Schema(description = "새로운 채널 설명", example = "공부 채널입니다.")
      String newDescription
  ) {

  }
}

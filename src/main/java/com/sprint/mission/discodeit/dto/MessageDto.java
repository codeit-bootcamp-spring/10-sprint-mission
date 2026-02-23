package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class MessageDto {

  @Schema(name = "messageCreateRequest", description = "메시지 생성 요청")
  public record Create(
      @Schema(description = "메시지 본문", example = "안녕하세요")
      String content,
      @Schema(description = "작성자 ID", example = "5cd294e0-4cde-4a67-8d5c-3f054927c595")
      UUID authorId,
      @Schema(description = "채널 ID", example = "0ad06ce5-bdfb-4304-b6eb-a133a4b49fb8")
      UUID channelId
  ) {

  }

  @Schema(name = "messageResponse", description = "메시지 응답")
  public record Response(
      @Schema(description = "메시지 ID", example = "4e23cb1a-e2ae-4171-811f-ccc4c13fc577")
      UUID id,
      @Schema(description = "메시지 본문", example = "안녕하세요")
      String content,
      @Schema(description = "작성자 ID", example = "5cd294e0-4cde-4a67-8d5c-3f054927c595")
      UUID authorId,
      @Schema(description = "채널 ID", example = "0ad06ce5-bdfb-4304-b6eb-a133a4b49fb8")
      UUID channelId,
      @Schema(description = "메시지 생성 시간", example = "2026-02-23T01:30:54Z")
      Instant createdAt,
      @Schema(description = "첨부 파일 ID 목록", example = "[0b71409f-f489-40a2-a075-c2c93640351c, 8c4e7c2b-5ac0-4d75-849a-b55db3a1c67f]")
      List<UUID> attachmentIds
  ) {

    public static Response of(Message message) {
      return new Response(
          message.getId(),
          message.getContent(),
          message.getAuthorId(),
          message.getChannelId(),
          message.getCreatedAt(),
          message.getAttachmentIds()
      );
    }
  }

  @Schema(name = "messageUpdateRequest", description = "수정할 Message 내용")
  public record Update(
      @Schema(description = "수정할 Message 내용", example = "메시지 수정")
      String newContent
  ) {

  }
}

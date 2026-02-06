package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class MessageDto {
    public record CreateRequest(
            @NotBlank
            String content,
            @NotNull
            UUID authorId,
            @NotNull
            UUID channelId,
            List<BinaryContentDto.CreateRequest> attachments

    ) {
        public CreateRequest { // attachments가 비어있으면 빈 리스트로 만듬
            if(attachments == null || attachments.isEmpty()) {
                attachments = List.of();
            }
        }
    }

    public record Response(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String content,
            UUID channelId,
            UUID authorId,
            List<UUID> attachmentIds
    ) {}

    public record UpdateRequest(
       String content // null이면 메시지 삭제됨
    ) {}


}

package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Message;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageDto {
    public record CreateRequest(
            @NotBlank
            String content,
            @NotBlank
            UUID authorId,
            @NotBlank
            UUID channelId,
            List<BinaryContentDto.CreateRequest> attachments

    ) {
        public CreateRequest { // attachments가 비어있으면 빈 리스트로 만듬
            if(attachments == null || attachments.isEmpty()) {
                attachments = List.of();
            }
        }
    }

    public record UpdateRequest(
       String content // null이면 메시지 삭제됨
    ) {}


}

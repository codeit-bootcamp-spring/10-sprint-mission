package com.sprint.mission.discodeit.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

public class MessageDto {
    public record CreateRequest(
            String content,
            UUID channelId,
            UUID authorId,
            List<BinaryContentDto> attachments // 첨부파일(선택)
    ) {}

    public record BinaryContentDto (
            byte[] data,
            String fileType,
            String fileName
    ) {}


    @Builder
    public record Response(
            UUID id,
            UUID channelId,
            UUID authorId,
            String content,
            List<UUID> attachmentId
    ) {}

    public record UpdateRequest (
            UUID id,
            String content,
            List<BinaryContentDto> attachments
    ) {}
}

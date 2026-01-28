package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public class BinaryContentDTO {

    public record Create(
            String fileName,
            byte[] bytes
    ) {}

    public record Response(
            UUID id,
            String fileName,
            byte[] bytes
    ) {
        public static Response of (BinaryContent content) {
            return new Response(
                    content.getId(),
                    content.getFileName(),
                    content.getBytes()
            );
        }
    }
}

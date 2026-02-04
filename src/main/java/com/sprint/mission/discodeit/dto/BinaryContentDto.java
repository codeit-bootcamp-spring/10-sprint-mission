package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public class BinaryContentDto {
    public record CreateRequest(
            byte[] bytes,
            String contentType,
            String fileName
    ){}

    public record Response(
            UUID id,
            byte[] bytes,
            String contentType,
            String fileName
    ){}

}

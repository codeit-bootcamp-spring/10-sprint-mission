package com.sprint.mission.discodeit.dto;

public class BinaryContentDto {

    public record BinaryContentRequest(
            String filePath,
            String contentType
    ) {}


}

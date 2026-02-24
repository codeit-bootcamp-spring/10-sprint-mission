package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContent;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "첨부 파일 응답")
public record BinaryContentResponse(
    @Schema(description = "첨부 파일 ID", example = "0b71409f-f489-40a2-a075-c2c93640351c")
    UUID id,
    @Schema(description = "첨부 파일 이름", example = "image1.png")
    String fileName,
    @Schema(description = "첨부 파일 크기", example = "1048576")
    long size,
    @Schema(description = "첨부 파일 타입", example = "image/png")
    String contentType,
    @Schema(description = "첨부 파일 byte 데이터", example = "U3dhZ2dlciB0ZXN0IGZpbGU=")
    byte[] bytes
) {

  public static BinaryContentResponse of(BinaryContent content) {
    return new BinaryContentResponse(
        content.getId(),
        content.getFileName(),
        content.getSize(),
        content.getContentType(),
        content.getBytes()
    );
  }
}

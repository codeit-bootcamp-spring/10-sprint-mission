package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "바이너리 콘텐츠 정보")
public class BinaryContentDto {

  @Schema(description = "바이너리 콘텐츠 ID", example = "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
  private UUID id;

  @Schema(description = "생성 일시", example = "2026-02-20T17:20:00Z")
  private Instant createdAt;

  @Schema(description = "수정 일시", example = "2026-02-20T17:21:00Z")
  private Instant updatedAt;

  @Schema(description = "파일명", example = "profile_image.png")
  private String fileName;

  @Schema(description = "파일 크기 (bytes)", example = "102400")
  private Long size;

  @Schema(description = "콘텐츠 타입", example = "image/png")
  private String contentType;

  @Schema(
      description = "파일 업로드 상태",
      example = "SUCCESS",
      allowableValues = {"PROCESSING", "SUCCESS", "FAIL"}
  )
  private BinaryContentStatus status;
}

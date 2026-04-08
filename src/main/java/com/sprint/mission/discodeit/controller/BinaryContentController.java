package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "BinaryContent")
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BasicBinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @GetMapping("/{binaryContentId}/download")
  @Operation(summary = "파일 다운로드", operationId = "download")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "파일 다운로드 성공",
          content = @Content(mediaType = "*/*",
              schema = @Schema(type = "string", format = "binary"))),
  })
  public ResponseEntity<?> download(
      @Parameter(description = "다운로드할 파일 ID", required = true)
      @PathVariable UUID binaryContentId) {
    log.info("REST request to download file: id={}", binaryContentId);
    BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);
    return binaryContentStorage.download(binaryContentDto);
  }

  @GetMapping("/{binaryContentId}")
  @Operation(summary = "첨부 파일 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공",
          content = @Content(schema = @Schema(implementation = BinaryContentDto.class))),
      @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "BinaryContent with id {binaryContentId} not found")))
  })
  public ResponseEntity<BinaryContentDto> find(
      @Parameter(description = "조회할 첨부 파일 ID", required = true)
      @PathVariable UUID binaryContentId) {
    log.debug("REST request to get binary content info: id={}", binaryContentId);
    return ResponseEntity.ok(binaryContentService.find(binaryContentId));
  }

  @GetMapping
  @Operation(summary = "여러 첨부 파일 조회")
  @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = BinaryContentDto.class))))
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @Parameter(description = "조회할 첨부 파일 ID 목록", required = true)
      @RequestParam List<UUID> binaryContentIds) {
    log.debug("REST request to get multiple binary contents info: count={}",
        binaryContentIds.size());
    return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
  }
}

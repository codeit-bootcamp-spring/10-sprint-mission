package com.sprint.mission.discodeit.binarycontent.controller;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentDto;
import com.sprint.mission.discodeit.binarycontent.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent")
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
@Slf4j
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @Operation(
      summary = "첨부 파일 조회"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "첨부 파일 조회 성공"
      ),

      @ApiResponse(
          responseCode = "404",
          description = "첨부 파일을 찾을 수 없음",
          content = @Content(
              examples = @ExampleObject(value = "BinaryContent with id {binaryContentId} not found")
          )
      )
  })
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContentDto> find(
      @Parameter(description = "조회할 첨부 파일 ID")
      @PathVariable UUID binaryContentId) {
    BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContent);
  }

  @Operation(
      summary = "여러 첨부 파일 조회",
      operationId = "findAllByIdIn"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "첨부 파일 목록 조회 성공"
      )
  })
  @GetMapping
  public ResponseEntity<List<BinaryContentDto>> findAll(@Parameter(description = "조회할 첨부 파일 ID 목록")
  @RequestParam List<UUID> binaryContentIds) {
    List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContents);
  }

  @Operation(
      summary = "파일 다운로드"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "첨부 파일 다운로드 성공"
      )
  })
  @GetMapping("/{binaryContentId}/download")
  public ResponseEntity<?> download(
      @Parameter(description = "다운로드할 첨부 파일 ID")
      @PathVariable UUID binaryContentId) {
    log.debug("[BINARY_CONTENT_DOWNLOAD] 파일 다운로드 요청 : binaryContentId={}", binaryContentId);
    BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);
    log.debug("[BINARY_CONTENT_DOWNLOAD] 파일 다운로드 응답 : fileName={}", binaryContentDto.fileName());
    return binaryContentStorage.download(binaryContentDto);
  }
}

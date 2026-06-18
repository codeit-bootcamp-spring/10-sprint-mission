package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "BinaryContent")
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  // BinaryContent 생성
  @Operation(summary = "첨부 파일 생성")
  @ApiResponse(responseCode = "201", description = "첨부 파일이 성공적으로 생성됨")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BinaryContentDto> createBinaryContent(
      @RequestPart("file") MultipartFile attachment) throws IOException {
    boolean hasAttachment = attachment != null && !attachment.isEmpty();
    log.info("[Controller] 첨부파일 생성 요청: hasAttachment={}", hasAttachment);

    BinaryContentDto dto = binaryContentService.create(attachment);
    log.debug("[Controller] 첨부파일 생성 응답 준비: id={}", dto.id());

    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  // BinaryContent 조회
  @Operation(summary = "첨부 파일 조회")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공"),
      @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/{binary-content-id}")
  public ResponseEntity<BinaryContentDto> findById(
      @PathVariable("binary-content-id") UUID binaryContentId) {
    BinaryContentDto dto = binaryContentService.findById(binaryContentId);
    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  // BinaryContent 다건 조회
  @Operation(summary = "여러 첨부 파일 조회")
  @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공")
  @GetMapping(params = "binaryContentIds")
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @RequestParam List<UUID> binaryContentIds) {
    List<BinaryContentDto> dto = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  // BinaryContent 삭제
  @Operation(summary = "첨부파일 삭제")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "첨부 파일이 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/{binary-content-id}")
  public ResponseEntity<Void> deleteById(@PathVariable("binary-content-id") UUID binaryContentId)
      throws IOException {
    log.info("[Controller] 첨부파일 삭제 요청: id={}", binaryContentId);

    binaryContentService.deleteById(binaryContentId);
    log.debug("[Controller] 첨부파일 삭제 응답 준비: id={}", binaryContentId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  // BinaryContent 다운로드
  @Operation(summary = "파일 다운로드")
  @ApiResponse(responseCode = "200", description = "파일 다운로드 성공",
      content = @Content(mediaType = "application/octet-stream", schema = @Schema(type = "string", format = "binary")
      ))
  @GetMapping("/{binary-content-id}/download")
  public ResponseEntity<?> download(@PathVariable("binary-content-id") UUID binaryContentId)
      throws IOException {
    log.info("[Controller] 첨부파일 다운로드 요청: id={}", binaryContentId);

    BinaryContentDto dto = binaryContentService.findById(binaryContentId);
    log.debug("[Controller] 첨부파일 다운로드 응답 준비: id={}", binaryContentId);

    return binaryContentStorage.download(dto);
  }
}

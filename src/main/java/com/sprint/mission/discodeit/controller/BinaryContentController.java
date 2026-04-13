package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@Validated
@Tag(name = "BinaryContent")
@Slf4j
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @GetMapping("/{binaryContentId}")
  @Operation(summary = "첨부 파일 조회")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공"),
      @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음")
  })
  public ResponseEntity<BinaryContentDto> find(
      @Parameter(description = "조회할 첨부 파일 ID", example = "0b71409f-f489-40a2-a075-c2c93640351c")
      @NotNull @PathVariable UUID binaryContentId
  ) {
    log.debug("파일 조회 요청: binaryContentId={}", binaryContentId);
    BinaryContentDto response = binaryContentService.findById(binaryContentId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping
  @Operation(summary = "여러 첨부 파일 조회")
  @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공")
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @Parameter(
          description = "조회할 첨부 파일 ID 목록",
          example = "[0b71409f-f489-40a2-a075-c2c93640351c, 8c4e7c2b-5ac0-4d75-849a-b55db3a1c67f]"
      )
      @NotNull @RequestParam("binaryContentIds") List<UUID> binaryContentIds
  ) {
    log.debug("파일 목록 조회 요청: binaryContentIdCount={}", binaryContentIds.size());
    List<BinaryContentDto> response = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{binaryContentId}/download")
  @Operation(summary = "파일 다운로드")
  @ApiResponse(responseCode = "200", description = "파일 다운로드 성공")
  public ResponseEntity<?> download(
      @Parameter(description = "다운로드할 파일 ID")
      @NotNull @PathVariable UUID binaryContentId
  ) {
    log.info("파일 다운로드 요청: binaryContentId={}", binaryContentId);
    BinaryContentDto response = binaryContentService.findById(binaryContentId);
    return binaryContentStorage.download(response);
  }
}

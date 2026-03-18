package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.exception.ErrorDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @Operation(summary = "첨부 파일 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "첨부 파일 조회 성공",
          content = @Content(schema = @Schema(implementation = BinaryContentDto.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "첨부 파일을 찾을 수 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value =
                  "{ \"status\": 404, \"error\": \"BINARY_CONTENT_NOT_FOUND\", "
                      + "\"message\": \"파일을 찾을 수 없습니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      )
  })
  @GetMapping(path = "/{binaryContentId}")
  public ResponseEntity<BinaryContentDto> find(
      @Parameter(description = "조회할 첨부 파일 ID") @PathVariable UUID binaryContentId) {
    BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);
    return ResponseEntity.status(HttpStatus.OK).body(binaryContent);
  }

  @Operation(summary = "여러 첨부 파일 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "첨부 파일 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = BinaryContentDto.class)))
      )
  })
  @GetMapping
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @Parameter(description = "조회할 첨부 파일 ID 목록") @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity.status(HttpStatus.OK).body(binaryContents);
  }

  @Operation(summary = "첨부 파일 다운로드")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "첨부 파일 다운로드 성공"),
      @ApiResponse(
          responseCode = "404",
          description = "첨부 파일을 찾을 수 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value =
                  "{ \"status\": 404, \"error\": \"BINARY_CONTENT_NOT_FOUND\", "
                      + "\"message\": \"파일을 찾을 수 없습니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      )
  })
  @GetMapping("/{binaryContentId}/download")
  public ResponseEntity<?> download(
      @Parameter(description = "다운로드할 첨부 파일 ID") @PathVariable UUID binaryContentId) {
    BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);
    return binaryContentStorage.download(binaryContentDto);
  }
}

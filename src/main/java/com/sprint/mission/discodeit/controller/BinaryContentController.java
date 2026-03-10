package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  public BinaryContentController(
      BinaryContentService binaryContentService,
      BinaryContentStorage binaryContentStorage
  ) {
    this.binaryContentService = binaryContentService;
    this.binaryContentStorage = binaryContentStorage;
  }

  @Operation(summary = "파일 다운로드", operationId = "download", tags = {"BinaryContent"})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "파일 다운로드 성공")
  })
  @RequestMapping(value = "/{binaryContentId}/download", method = RequestMethod.GET)
  public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
    BinaryContentResponse response = binaryContentService.find(binaryContentId);

    BinaryContentDto dto = new BinaryContentDto(
        response.id(),
        response.fileName(),
        response.size(),
        response.contentType()
    );

    return binaryContentStorage.download(dto);
  }

  @Operation(summary = "첨부 파일 조회", operationId = "find", tags = {"BinaryContent"})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공"),
      @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음")
  })
  @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
  public ResponseEntity<BinaryContentDto> find(@PathVariable UUID binaryContentId) {
    BinaryContentResponse response = binaryContentService.find(binaryContentId);

    return ResponseEntity.ok(
        new BinaryContentDto(
            response.id(),
            response.fileName(),
            response.size(),
            response.contentType()
        )
    );
  }

  @Operation(summary = "여러 첨부 파일 조회", operationId = "findAllByIdIn", tags = {"BinaryContent"})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공")
  })
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {

    return ResponseEntity.ok(
        binaryContentService.findAllByIdIn(binaryContentIds).stream()
            .map(response -> new BinaryContentDto(
                response.id(),
                response.fileName(),
                response.size(),
                response.contentType()
            ))
            .toList()
    );
  }
}
package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
@Tag(name = "BinaryContent", description = "BinaryContent contrlller 입니다.")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @RequestMapping(method = RequestMethod.GET)
  @Operation(summary = "여러 첨부 파일 조회", operationId = "findAllByIdIn")
  public ResponseEntity<List<BinaryContentResponseDto>> getBinaryContentByIds(
      @Parameter(name = "binaryContentIds", description = "조회할 첨부 파일 ID 목록") @RequestParam("binaryContentIds") List<UUID> binaryContentIds)
      throws
      IOException {
    return ResponseEntity.status(HttpStatus.OK)
        .body(binaryContentService.findAllByIdIn(binaryContentIds));
  }

  @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
  @Operation(summary = "첨부 파일 조회", operationId = "find")
  public ResponseEntity<BinaryContentResponseDto> getBinaryContent(
      @Parameter(name = "binaryContentId", description = "조회할 첨부 파일 ID") @PathVariable("binaryContentId") UUID binaryContentId)
      throws
      IOException {
    return ResponseEntity.status(HttpStatus.OK)
        .body(binaryContentService.findById(binaryContentId));
  }

}

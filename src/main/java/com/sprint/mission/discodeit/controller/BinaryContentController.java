package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
@Tag(name = "BinaryContent", description = "첨부 파일 API")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @RequestMapping(path = "/{binaryContentId}", method = RequestMethod.GET)
  @Operation(summary = "첨부 파일 조회")
  @ApiResponses({
          @ApiResponse(
                  responseCode = "200", description = "첨부 파일 조회 성공",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = BinaryContentDto.class)
                  )
          ),
          @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음",
                  content = @Content(
                          examples = @ExampleObject(value = "BinaryContent with id {binaryContentId} not found")
                  )
          )
  })
  public ResponseEntity<BinaryContent> findBinaryContent(@PathVariable UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentService.find(binaryContentId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContent);
  }



  @RequestMapping(method = RequestMethod.GET)
  @Operation(summary = "여러 첨부 파일 조회")
  @ApiResponse(
          responseCode = "200", description = "첨부 파일 목록 조회 성공",
          content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = BinaryContentDto.class)
          )
  )
  public ResponseEntity<List<BinaryContent>> findBinaryContents(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContents);
  }
}

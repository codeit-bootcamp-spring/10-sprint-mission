package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
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
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentMapper binaryContentMapper;

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


    @RequestMapping(path = "/{binaryContentId}/download", method = RequestMethod.GET)
    @Operation(summary = "파일 다운로드")
    // 다운로드는 JSON 응답이 아니므로 schema 속성은 제외합니다.
    @ApiResponse(responseCode = "200", description = "파일 다운로드 성공")
    public ResponseEntity<Resource> download(@PathVariable UUID binaryContentId) {

        // 컨트롤러는 서비스에게 "이 ID의 파일 다운로드 응답 객체를 만들어와"라고 지시만 합니다.
        return binaryContentService.download(binaryContentId);
    }


}

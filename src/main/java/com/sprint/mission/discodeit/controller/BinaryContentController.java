package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @Operation(summary = "첨부 파일 조회", operationId = "find")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공"),
            @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "BinaryContent with id {binaryContentId} not found")))
    })
    @RequestMapping(method = RequestMethod.GET,value = "/{binaryContentId}")
    public ResponseEntity<BinaryContentDto> find(@Parameter(description = "조회할 첨부 파일 ID") @PathVariable UUID binaryContentId){
        return ResponseEntity.ok(binaryContentService.find(binaryContentId));
    }

    // 여러 개 조회
    @Operation(summary = "여러 첨부 파일 조회", operationId = "findAllByIdIn")
    @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentDto>> findAll(
            @Parameter(description = "조회할 첨부 파일 ID 목록") @RequestParam("binaryContentIds") List<UUID> binaryContentIds){
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
    }

    @Operation(summary = "파일 다운로드", operationId = "download")
    @ApiResponse(responseCode = "200", description = "파일 다운로드 성공",
            content = @Content(mediaType = "*/*", schema = @Schema(type = "string", format = "binary")))
    @RequestMapping(method = RequestMethod.GET, value = "/{binaryContentId}/download")
    public ResponseEntity<?> download(@Parameter(description = "다운로드할 파일 ID") @PathVariable UUID binaryContentId){
        log.info("REST 요청 - 첨부 파일 다운로드 시작: binaryContentId={}", binaryContentId);
        BinaryContentDto dto = binaryContentService.find(binaryContentId);
        return binaryContentStorage.download(dto);
    }
}
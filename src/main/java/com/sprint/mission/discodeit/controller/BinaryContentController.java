package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@Tag(name = "BinaryContent", description = "첨부 파일 API")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    // 바이너리 파일 1개 조회
    @GetMapping(value = "/{binaryContentId}")
    @Operation(summary = "첨부 파일 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "첨부 파일 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "첨부 파일을 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(value = "binaryContentId:{binaryContentId}를 가진 BinaryContent를 찾지 못했습니다")
                    )
            )
    })
    public ResponseEntity<BinaryContentDto> find(
            @Parameter(description = "조회할 첨부 파일 ID")
            @PathVariable(value = "binaryContentId") UUID binaryContentId) {
        BinaryContentDto response = binaryContentService.find(binaryContentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 바이너리 파일 여러 개 조회
    @GetMapping
    @Operation(summary = "여러 첨부 파일 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "첨부 파일 목록 조회 성공",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = BinaryContentDto.class)
                            )
                    )
            )
    })
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
            @Parameter(description = "조회할 첨부 파일 ID 목록")
            @RequestParam List<UUID> binaryContentIds) {
        List<BinaryContentDto> response = binaryContentService.findAllByIdIn(binaryContentIds);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 파일 다운로드
    @Operation(summary = "파일 다운로드")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "파일 다운로드 성공",
                    content = @Content(
                            schema = @Schema(implementation = Resource.class)
                    )
            )
    })
    @GetMapping(value = "/{binaryContentId}/download")
    public ResponseEntity<?> download(
            @Parameter(description = "다운로드할 파일 ID")
            @PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContentDto data = binaryContentService.find(binaryContentId);
        return binaryContentStorage.download(data);
    }
}

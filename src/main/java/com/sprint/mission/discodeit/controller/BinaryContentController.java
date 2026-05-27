package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentDTO;
import com.sprint.mission.discodeit.service.BinaryContentService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
@Tag(name = "BinaryContent", description = "첨부 파일 API")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    // binary-Content 단건 조회 - GET /api/binaryContents/{binaryContentId}
    @GetMapping("/{binaryContentId}")
    @Operation(summary = "첨부파일 조회", operationId = "find")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "첨부 파일 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BinaryContentDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "첨부 파일을 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject("BinaryContentId not found")
                    )
            )
    })
    public ResponseEntity<BinaryContentDTO> getBinaryContent(
            @Parameter(
                    description = "조회할 첨부파일 Id",
                    example = "123e4567-e89b-12d3-a456-426655440000",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")

            )
            @PathVariable UUID binaryContentId
    ){
        log.debug("BinaryContentId: {}", binaryContentId);
        return ResponseEntity.ok(binaryContentService.find(binaryContentId));
    }

    // binary-content 다건 조회 - GET /api/binaryContents?binaryContentIds=binaryContentId1?binaryContentId2
    @GetMapping
    @Operation(summary = "여러 첨부 파일 조회")
    @ApiResponse(
            responseCode = "200",
            description = "첨부파일 다건 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = BinaryContentDTO.class)
                    )
            )
    )
    public ResponseEntity<List<BinaryContentDTO>> getBinaryContents(
            @Parameter(
                    description = "조회할 첨부파일 ID 목록. 예) binaryContentIds=id1&id2",
                    example = "binaryContentIds=123e4567-e89b-12d3-a456-426655440000&550e8400-e29b-41d4-a716-446655440000",
                    required = true,
                    schema = @Schema(type = "array", format = "uuid")
            )
            @RequestParam List<UUID> binaryContentIds
    ){
        log.debug("BinaryContentIds: {}", binaryContentIds);
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
    }

    /*
     * 파일 다운로드
     */
    @GetMapping("/{binaryContentId}/download")
    @Operation(summary = "파일 다운로드", operationId = "download")
    @ApiResponse(
            responseCode = "200",
            description = "파일 다운로드 성공",
            content=@Content(schema = @Schema(type= "string", format = "binary"))
    )
    public ResponseEntity<?> download(@PathVariable UUID binaryContentId){
        log.info("파일 다운로드 요청 - binaryContentId: {}", binaryContentId);
        return binaryContentService.download(binaryContentId);
    }

}

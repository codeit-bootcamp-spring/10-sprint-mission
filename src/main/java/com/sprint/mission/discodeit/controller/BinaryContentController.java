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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "BinaryContent")
@RequestMapping("/api/binaryContents")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    // BinaryContent 생성
    @Operation(summary = "첨부 파일 생성")
    @ApiResponse(responseCode = "201", description = "첨부 파일이 성공적으로 생성됨")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BinaryContentDto> createBinaryContent(@RequestPart("file") MultipartFile attachment) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(binaryContentService.create(attachment));
    }

    // BinaryContent 조회
    @Operation(summary = "첨부 파일 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공"),
            @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "/{binary-content-id}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentDto> findById(@PathVariable("binary-content-id") UUID binaryContentId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(binaryContentService.findById(binaryContentId));
    }

    // BinaryContent 다건 조회
    @Operation(summary = "여러 첨부 파일 조회")
    @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공")
    @RequestMapping(params = "binaryContentIds", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(@RequestParam List<UUID> binaryContentIds) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(binaryContentService.findAllByIdIn(binaryContentIds));
    }

    // BinaryContent 삭제
    @Operation(summary = "첨부파일 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "첨부 파일이 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "/{binary-content-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteById(@PathVariable("binary-content-id") UUID binaryContentId) throws IOException {
        binaryContentService.deleteById(binaryContentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // BinaryContent 다운로드
    @Operation(summary = "파일 다운로드")
    @ApiResponse(responseCode = "200", description = "파일 다운로드 성공",
            content = @Content(mediaType = "application/octet-stream", schema = @Schema(type = "string", format = "binary")
    ))
    @RequestMapping(value = "/{binary-content-id}/download", method = RequestMethod.GET)
    public ResponseEntity<?> download(@PathVariable("binary-content-id") UUID binaryContentId) throws IOException {
        return binaryContentStorage.download(binaryContentService.findById(binaryContentId));
    }
}

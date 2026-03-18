package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContentOwnerType;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
@Tag(name = "BinaryContent", description = "첨부 파일 API")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "첨부 파일 생성")
    @ApiResponse(responseCode = "200", description = "첨부 파일 생성 성공")
    public ResponseEntity<UUID> createBinaryContent(
            @Parameter(description = "파일 소유 타입", example = "USER")
            @RequestParam BinaryContentOwnerType type,
            @RequestPart("file") MultipartFile file
    ) {
        BinaryContentRequest request = new BinaryContentRequest(type, file);
        UUID id = binaryContentService.createBinaryContent(request);

        return ResponseEntity.ok(id);
    }

    @GetMapping("/{binaryContentId}")
    @Operation(summary = "첨부 파일 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공"),
            @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음")
    })
    public ResponseEntity<BinaryContentDto> findBinaryContent(
            @Parameter(description = "조회할 첨부 파일 ID", example = "a1636f5f-72ab-497f-a6ff-72e4eb6f9f54")
            @PathVariable UUID binaryContentId
    ) {
        BinaryContentDto response = binaryContentService.findBinaryContent(binaryContentId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "여러 첨부 파일 조회")
    @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공")
    public ResponseEntity<List<BinaryContentDto>> findAllBinaryContents(
            @Parameter(
                    description = "조회할 첨부 파일 ID 목록",
                    example = "a1636f5f-72ab-497f-a6ff-72e4eb6f9f54,af1645f1-94f3-4d62-867f-8827e4c5fa42"
            )
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds
    ) {
        List<BinaryContentDto> responses = binaryContentService.findAllByIdIn(binaryContentIds);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{binaryContentId}/download")
    public ResponseEntity<?> downloadBinaryContent(
            @PathVariable UUID binaryContentId
    ) {
        return binaryContentService.downloadBinaryContent(binaryContentId);
    }

    @DeleteMapping("/{binaryContentId}")
    @Operation(summary = "첨부 파일 삭제")
    @ApiResponse(responseCode = "204", description = "첨부 파일 삭제 성공")
    public ResponseEntity<Void> deleteBinaryContent(
            @PathVariable UUID binaryContentId
    ) {
        binaryContentService.deleteBinaryContent(binaryContentId);

        return ResponseEntity.noContent().build();
    }
}

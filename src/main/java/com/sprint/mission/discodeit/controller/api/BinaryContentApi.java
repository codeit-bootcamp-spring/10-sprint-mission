package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "첨부 파일 및 바이너리 컨텐츠 관리 API")
public interface BinaryContentApi {

    @Operation(summary = "첨부 파일 메타데이터 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음")
    })
    @GetMapping("/{binaryContentId}")
    ResponseEntity<BinaryContentDto.Response> findBinaryContent(
            @Parameter(description = "조회할 첨부 파일 ID") @PathVariable("binaryContentId") UUID binaryContentId
    );

    @Operation(summary = "여러 첨부 파일 메타데이터 조회")
    @GetMapping
    ResponseEntity<List<BinaryContentDto.Response>> findAll(
            @Parameter(description = "조회할 첨부 파일 ID 목록") @RequestParam("binaryContentIds") List<UUID> binaryContentIds
    );

    @Operation(summary = "파일 다운로드", description = "실제 파일 컨텐츠를 스트림으로 다운로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음")
    })
    @GetMapping("/{binaryContentId}/download")
    ResponseEntity<?> download(
            @Parameter(description = "다운로드할 첨부 파일 ID") @PathVariable("binaryContentId") UUID binaryContentId
    );
}

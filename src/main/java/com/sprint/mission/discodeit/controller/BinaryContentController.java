package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @RequestMapping(method = RequestMethod.GET)
    @ApiResponse(
        responseCode = "200",
        description = "첨부 파일 목록 조회 성공",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(
                schema = @Schema(implementation = BinaryContentDto.class)
            )
        )
    )
    public ResponseEntity<List<BinaryContentDto>> getBinaryContents(
        @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {

        return new ResponseEntity<>(binaryContentService.findAllByIdIn(binaryContentIds),
            HttpStatus.OK);
    }

    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "첨부 파일 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    implementation = BinaryContentDto.class
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "첨부 파일을 찾을 수 없음",
            content = @Content(
                examples = @ExampleObject("BinaryContent with id {binaryContentId} not found")
            )
        )
    })
    public ResponseEntity<BinaryContentDto> getBinaryContent(
        @PathVariable UUID binaryContentId) {
        return new ResponseEntity<>(binaryContentService.find(binaryContentId), HttpStatus.OK);
    }

    @GetMapping(value = "/{binaryContentId}/download")
    public ResponseEntity<?> downloadBinaryContent(
        @PathVariable UUID binaryContentId) {

        return binaryContentStorage.download(binaryContentService.find(binaryContentId));
    }

}

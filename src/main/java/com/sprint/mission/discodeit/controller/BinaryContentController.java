package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(method = RequestMethod.GET)
    @ApiResponse(
        responseCode = "200",
        description = "첨부 파일 목록 조회 성공",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(
                schema = @Schema(implementation = BinaryContentResponseDTO.class)
            )
        )
    )
    public ResponseEntity<List<BinaryContentResponseDTO>> getBinaryContents(
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
                    implementation = BinaryContentResponseDTO.class
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
    public ResponseEntity<BinaryContentResponseDTO> getBinaryContent(
        @PathVariable UUID binaryContentId) {
        return new ResponseEntity<>(binaryContentService.find(binaryContentId), HttpStatus.OK);
    }

}

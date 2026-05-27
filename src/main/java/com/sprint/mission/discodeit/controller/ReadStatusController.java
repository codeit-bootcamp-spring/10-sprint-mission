package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.response.ReadStatusDTO;
import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/readStatuses")
@Tag(name = "ReadStatus", description = "ReadStatus API")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    // ReadStatus 정보 생성 - POST /api/readStatuses (201 Created)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Message 읽음 상태 생성", operationId = "create_1")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Message 읽음 사태가 성공적으로 생성됨",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadStatusDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel 혹은 User를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject("Channel or user not found"))
            )
    })
    public ResponseEntity<ReadStatusDTO> postReadStatus(@Valid @RequestBody ReadStatusCreateRequest request){
        log.debug("메시지 읽음 상태 생성 - userId : {}, channelId: {}, lastReadAt: {}", request.userId(), request.channelId(), request.lastReadAt());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(readStatusService.create(request));
    }

    // ReadStatus 정보 수정 - PATCH /api/readStatuses/{readStatusId} (200 OK)
    @PatchMapping("/{readStatusId}")
    @Operation(summary = "Message 읽음 상태 수정", operationId = "update_1")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 읽음 상태가 성공적으로 수정됨",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadStatusDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message 읽음 상태 찾을 수 없음",
                    content = @Content(examples = @ExampleObject("ReadStatus not found"))
            )
    })
    public ResponseEntity<ReadStatusDTO> updateReadStatus(
            @Parameter(
                    description = "수정할 readStatusId",
                    example = "123e4567-e89b-12d3-a456-426655440000",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable UUID readStatusId,
            @Valid @RequestBody ReadStatusUpdateRequest request
    ){
        log.debug("수정할 readStatusId : {}, lastReadAt : {}: ", readStatusId, request.newLastReadAt());
        return ResponseEntity.ok(readStatusService.update(readStatusId, request));
    }

    // 특정 사용자의 메시지 수신 정보 조회 - GET /api/readStatuses?userId=userId
    @GetMapping
    @Operation(summary = "User의 읽음 상태 목록 조회", operationId = "findAllByUserId")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 읽은 상태 목록 조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ReadStatusDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject("User not found"))
            )
    })
    public List<ReadStatusDTO> getReadStatusByUserId(
            @Parameter(description = "조회할 userId", example = "123e4567-e89b-12d3-a456-426655440000", required = true)
            @RequestParam UUID userId){
        log.debug("user의 읽음 상태 조회 : {}", userId);
        return readStatusService.findAllByUserId(userId);
    }
}

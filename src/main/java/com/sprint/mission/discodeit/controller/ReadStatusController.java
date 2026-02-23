package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/readStatus")
public class ReadStatusController {

    private final ReadStatusService readStatusService;


    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiResponse(
        responseCode = "200",
        description = "Message 읽음 상태 목록 조회 성공",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ReadStatus.class))
        )
    )
    public List<ReadStatusResponseDTO> getReadStatus(@PathVariable UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.POST)
    @ResponseBody
    @ApiResponses({
        @ApiResponse(
            responseCode = "404",
            description = "Channel 또는 User를 찾을 수 없음",
            content = @Content(
                examples = @ExampleObject("Channel | User with id {channelId | userId} not found")
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "이미 읽음 상태가 존재함",
            content = @Content(
                examples = @ExampleObject("ReadStatus with userId {userId} and channelId {channelId} already exists")
            )
        ),
        @ApiResponse(
            responseCode = "201",
            description = "Message 읽음 상태가 성공적으로 생성됨",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReadStatusResponseDTO.class)
            )
        )
    }
    )
    public List<ReadStatusResponseDTO> createReadStatus(@PathVariable UUID channelId) {
        return readStatusService.create(channelId);
    }

    @RequestMapping(value = " /{readStatusId}", method = RequestMethod.PATCH)
    @ResponseBody
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Message 읽음 상태가 성공적으로 수정됨",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReadStatusResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Message 읽음 상태를 찾을 수 없음",
            content = @Content(
                examples = @ExampleObject("ReadStatus with id {readStatusId} not found")
            )
        )
    }
    )
    public ReadStatusResponseDTO editReadStatus(
        @Valid @RequestBody ReadStatusUpdateRequestDTO req, @PathVariable UUID readStatusId) {
        return readStatusService.update(readStatusId, req);
    }


}
